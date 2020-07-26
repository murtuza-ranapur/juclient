package com.jucient.spec.spring.processor;

import com.juclient.core.parser.RequestType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestTypeMap {
    private static final Map<Class<?>, RequestType> classRequestType = new HashMap<>();
    private static final Map<RequestMethod, RequestType> methodRequestTypeMap = new HashMap<>();

    static {
        classRequestType.put(RequestMapping.class, RequestType.UNKNOWN);
        classRequestType.put(PostMapping.class, RequestType.POST);
        classRequestType.put(GetMapping.class, RequestType.GET);
        classRequestType.put(PutMapping.class, RequestType.PUT);
        classRequestType.put(DeleteMapping.class, RequestType.DELETE);
        classRequestType.put(PatchMapping.class, RequestType.PATCH);

        methodRequestTypeMap.put(RequestMethod.GET, RequestType.GET);
        methodRequestTypeMap.put(RequestMethod.DELETE, RequestType.DELETE);
        methodRequestTypeMap.put(RequestMethod.POST, RequestType.POST);
        methodRequestTypeMap.put(RequestMethod.PUT, RequestType.PUT);
        methodRequestTypeMap.put(RequestMethod.PATCH, RequestType.PATCH);
        methodRequestTypeMap.put(RequestMethod.HEAD, RequestType.HEAD);
        methodRequestTypeMap.put(RequestMethod.OPTIONS, RequestType.OPTIONS);
        methodRequestTypeMap.put(RequestMethod.TRACE, RequestType.TRACE);
    }

    public static Optional<RequestType> findRequestType(Class<?> annotation){
        return Optional.ofNullable(classRequestType.get(annotation));
    }

    public static RequestType getRequestType(RequestMethod requestMethod){
        return methodRequestTypeMap.get(requestMethod);
    }
}
