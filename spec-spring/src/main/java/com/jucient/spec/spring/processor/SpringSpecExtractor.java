package com.jucient.spec.spring.processor;

import com.juclient.core.annotations.ApiClient;
import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.RequestType;
import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripheral;
import com.juclient.core.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SpringSpecExtractor implements Extractor {
    private static final Set<Class<?>> supportedParameterAnnotations = Stream
            .of(RequestBody.class, RequestHeader.class, RequestParam.class, PathVariable.class)
            .collect(Collectors.toSet());

    @Override
    public List<UnderstandableFunction> extract(String packagePath) {
        List<UnderstandableFunction> functions = new LinkedList<>();
        List<Class<?>> controllers = ClassUtils.getClassesByAnnotation(RestController.class, packagePath);
        for (Class<?> controller : controllers) {
            String base = findUrlFromRequestMapping(controller);
            List<Method> targetMethod = getTargetAPIMethod(controller);
            log.info("Found {} target methods in {} controller", targetMethod.size(), controller);
            for (Method method : targetMethod) {
                UnderstandableFunction understandableFunction = convertToUnderstandableFunction(method, base);
                functions.add(understandableFunction);
            }
        }
        return functions;
    }

    @Override
    public String specName() {
        return null;
    }

    private UnderstandableFunction convertToUnderstandableFunction(Method method, String base) {
        UnderstandableFunction understandableFunction = new UnderstandableFunction();
        putTypeAndPath(understandableFunction, method, base);
        understandableFunction.setClassName(method.getDeclaringClass().getName());
        understandableFunction.setFunctionName(method.getName());
        understandableFunction.setRequestReturnType(method.getGenericReturnType());
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Optional<Annotation> supportedAnnotation = Stream.of(parameter.getAnnotations())
                    .filter(anno -> supportedParameterAnnotations.contains(anno.annotationType())).findFirst();
            supportedAnnotation
                    .ifPresent(annotation -> setAppropriateValue(parameter, annotation, understandableFunction));
        }
        return understandableFunction;
    }

    private void setAppropriateValue(Parameter parameter, Annotation annotation,
            UnderstandableFunction understandableFunction) {
        Class<? extends Annotation> aClass = annotation.annotationType();
        if (RequestBody.class.equals(aClass)) {
            understandableFunction.setRequestBodyType(parameter.getParameterizedType());
        } else if (RequestParam.class.equals(aClass)) {
            RequestParam requestParam = (RequestParam) annotation;
            String paramName = requestParam.value();
            if (paramName.isBlank()) {
                paramName = parameter.getName();
            }
            UnderstandableRequestPeripheral requestPeripherals = new UnderstandableRequestPeripheral();
            requestPeripherals.setName(paramName);
            requestPeripherals.setType(parameter.getParameterizedType());
            requestPeripherals.setIsRequired(requestParam.required());
            understandableFunction.getRequestParam().add(requestPeripherals);
        } else if (RequestHeader.class.equals(aClass)) {
            RequestHeader requestHeader = (RequestHeader) annotation;
            String paramName = requestHeader.value();
            if (paramName.isBlank()) {
                paramName = parameter.getName();
            }
            UnderstandableRequestPeripheral requestPeripherals = new UnderstandableRequestPeripheral();
            requestPeripherals.setName(paramName);
            requestPeripherals.setType(parameter.getParameterizedType());
            requestPeripherals.setIsRequired(requestHeader.required());
            understandableFunction.getRequestHeaders().add(requestPeripherals);
        } else {
            // Path Param
            PathVariable pv = (PathVariable) annotation;
            String pathVariableName = pv.name();
            if (pathVariableName.isBlank()) {
                pathVariableName = parameter.getName();
            }
            understandableFunction.getPathParams().add(pathVariableName);
        }
    }

    private void putTypeAndPath(UnderstandableFunction understandableFunction, Method method, String base) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            Optional<RequestType> typeOptional = RequestTypeMap.findRequestType(annotation.annotationType());
            if (typeOptional.isPresent()) {
                Optional<String> path = getPathBasedOnRequestType(typeOptional.get(), method);
                if (path.isPresent()) {
                    understandableFunction.setRequestType(typeOptional.get());
                    understandableFunction.setUrl(base + path.get());
                } else {
                    // If path is not present case if that it's annotated with @RequestMapping
                    String urlPath = "";
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    if (requestMapping.value().length > 0) {
                        urlPath = requestMapping.value()[0];
                    } else {
                        urlPath = requestMapping.name();
                    }
                    understandableFunction.setUrl(base + urlPath);
                    // If no request method present use GET as default or if multiple present pick first
                    if (requestMapping.method().length > 0) {
                        understandableFunction
                                .setRequestType(RequestTypeMap.getRequestType(requestMapping.method()[0]));
                    } else {
                        understandableFunction.setRequestType(RequestType.GET);
                    }
                }
                break;
            }
        }
    }

    private String findUrlFromRequestMapping(Class<?> restClass) {
        RequestMapping annotation = restClass.getAnnotation(RequestMapping.class);
        if (annotation != null) {
            if (annotation.value().length > 0) {
                return annotation.value()[0];
            }
            return annotation.name();
        }
        return "";
    }

    private List<Method> getTargetAPIMethod(Class<?> restClass) {
        Method[] methods = restClass.getMethods();
        List<Method> targetMethods = new LinkedList<>();
        for (Method method : methods) {
            ApiClient apiClient = method.getAnnotation(ApiClient.class);
            if (apiClient != null) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (RequestTypeMap.findRequestType(annotation.annotationType()).isPresent()) {
                        targetMethods.add(method);
                        break;
                    }
                }
            }
        }
        return targetMethods;
    }

    private Optional<String> getPathBasedOnRequestType(RequestType type, Method method) {
        String path = "";
        switch (type) {
        case GET:
            GetMapping getMapping = method.getDeclaredAnnotation(GetMapping.class);
            if (getMapping.value().length > 0) {
                path = getMapping.value()[0];
            } else {
                path = getMapping.name();
            }
            break;
        case PUT:
            PutMapping putMapping = method.getDeclaredAnnotation(PutMapping.class);
            if (putMapping.value().length > 0) {
                path = putMapping.value()[0];
            } else {
                path = putMapping.name();
            }
            break;
        case POST:
            PostMapping postMapping = method.getDeclaredAnnotation(PostMapping.class);
            if (postMapping.value().length > 0) {
                path = postMapping.value()[0];
            } else {
                path = postMapping.name();
            }
            break;
        case DELETE:
            DeleteMapping deleteMapping = method.getDeclaredAnnotation(DeleteMapping.class);
            if (deleteMapping.value().length > 0) {
                path = deleteMapping.value()[0];
            } else {
                path = deleteMapping.name();
            }
            break;
        case PATCH:
            PatchMapping patchMapping = method.getDeclaredAnnotation(PatchMapping.class);
            if (patchMapping.value().length > 0) {
                path = patchMapping.value()[0];
            } else {
                path = patchMapping.name();
            }
            break;
        default:
            return Optional.empty();
        }
        return Optional.of(path);
    }
}
