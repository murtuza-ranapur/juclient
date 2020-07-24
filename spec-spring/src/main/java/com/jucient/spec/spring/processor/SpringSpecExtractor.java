package com.jucient.spec.spring.processor;

import com.juclient.core.annotations.ApiClient;
import com.juclient.core.parser.Extractor;
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
        }
        return functions;
    }

    private String findUrlFromRequestMapping(Class<?> restClass){
        RequestMapping annotation = restClass.getAnnotation(RequestMapping.class);
        if(annotation != null){
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
                    if(annotation.annotationType() == RequestMapping.class ||
                            supportedAnnotations.contains(annotation.annotationType())){
                        targetMethods.add(method);
                        break;
                    }
                }
            }
        }
        return targetMethods;
    }
}
