package com.jucient.spec.spring.processor;

import com.juclient.core.annotations.ApiClient;
import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.RequestType;
import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SpringSpecExtractor implements Extractor {
    private static final Set<Class<?>> supportedAnnotations = Stream.of(PostMapping.class, GetMapping.class,
            PutMapping.class, DeleteMapping.class).collect(Collectors.toSet());

    @Override
    public List<UnderstandableFunction> extract(String packagePath) {
        List<UnderstandableFunction> functions = new LinkedList<>();
        List<Class<?>> controllers = ClassUtils.getClassesByAnnotation(RestController.class, packagePath);
        for (Class<?> controller : controllers) {
            String base = findUrlFromRequestMapping(controller);
            List<Method> targetMethod = getTargetAPIMethod(controller);
            log.info("Found {} target methods in {} controller",targetMethod.size(), controller);
            for (Method method : targetMethod) {
                UnderstandableFunction understandableFunction = convertToUnderstandableFunction(method);
            }
        }
        return functions;
    }

    private UnderstandableFunction convertToUnderstandableFunction(Method method) {
        UnderstandableFunction understandableFunction = new UnderstandableFunction();
        putTypeAndPath(understandableFunction, method);
        return null;
    }

    private void putTypeAndPath(UnderstandableFunction understandableFunction, Method method) {
        Annotation [] annotations = method.getAnnotations();
        ApiClient apiClient = method.getAnnotation(ApiClient.class);

//        RequestTypeMap.findRequestType()
    }

    private String findUrlFromRequestMapping(Class<?> restClass){
        RequestMapping annotation = restClass.getAnnotation(RequestMapping.class);
        if(annotation != null){
            if(annotation.value().length>0){
                return annotation.value()[0];
            }
            return annotation.name();
        }
        return "";
    }

    private List<Method> getTargetAPIMethod(Class<?> restClass){
        Method [] methods = restClass.getMethods();
        List<Method> targetMethods = new LinkedList<>();
        for (Method method : methods) {
            ApiClient apiClient = method.getAnnotation(ApiClient.class);
            if(apiClient != null){
                Annotation [] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    if(RequestTypeMap.findRequestType(annotation.annotationType()).isPresent()){
                        targetMethods.add(method);
                        break;
                    }
                }
            }
        }
        return targetMethods;
    }

    private String requestTypeWiseDataExtraction(RequestType type, Method method){
        switch (type){
            case GET:
                GetMapping getMapping = method.getDeclaredAnnotation(GetMapping.class);
                if(getMapping.value().length>0){
                    return getMapping.value()[0];
                }
                return getMapping.name();
            case PUT:
                PutMapping putMapping = method.getDeclaredAnnotation(PutMapping.class);
                if(putMapping.value().length>0){
                    return putMapping.value()[0];
                }
                return putMapping.name();
            case POST:
                PostMapping postMapping = method.getDeclaredAnnotation(PostMapping.class);
                if(postMapping.value().length>0){
                    return postMapping.value()[0];
                }
                return postMapping.name();
            case DELETE:
                DeleteMapping deleteMapping = method.getDeclaredAnnotation(DeleteMapping.class);
                if(deleteMapping.value().length>0){
                    return deleteMapping.value()[0];
                }
                return deleteMapping.name();
            case UNKNOWN:
                RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
                return requestMapping.name();
        }
        return "";
    }
}
