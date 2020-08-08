package com.juclient.core.specs;

import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripherals;
import com.juclient.core.specs.enums.InBuiltTypes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public class SpecBuilder {
    private final Spec spec;
    private final Map<String, UnderstandableType> typeMap;
    private final Map<String, UnderstandableEnum> enumMap;

    public SpecBuilder() {
        this.spec = new Spec();
        this.typeMap = new HashMap<>();
        this.enumMap = new HashMap<>();
    }

    public SpecBuilder add(UnderstandableFunction understandableFunction) {
        EndPoint endPoint = new EndPoint();
        endPoint.setUrl(understandableFunction.getUrl());
        endPoint.setPathParams(understandableFunction.getPathParams());
        endPoint.setRequestType(understandableFunction.getRequestType().name());
        endPoint.setSuggestedMethodName(understandableFunction.getFunctionName());
        endPoint.setGroup(understandableFunction.getClassName());
        endPoint.setRequestBodyType(extractType(understandableFunction.getRequestBodyType()));
        endPoint.setReturnType(extractType(understandableFunction.getRequestReturnType()));
        endPoint.setRequestParams(extractPeripherals(understandableFunction.getRequestParam()));
        endPoint.setHeaders(extractPeripherals(understandableFunction.getRequestHeaders()));
        spec.getEndPoints().add(endPoint);
        return this;
    }

    private List<RequestPeripherals> extractPeripherals(List<UnderstandableRequestPeripherals> requestParam) {
        List<RequestPeripherals> peripheralsList = new ArrayList<>(requestParam.size());
        for (UnderstandableRequestPeripherals understandableRequestPeripheral : requestParam) {
            RequestPeripherals requestPeripherals = new RequestPeripherals();
            requestPeripherals.setName(understandableRequestPeripheral.getName());
            requestPeripherals.setType(extractType(understandableRequestPeripheral.getType()));
            requestPeripherals.setRequired(understandableRequestPeripheral.getIsRequired());
            peripheralsList.add(requestPeripherals);
        }
        return peripheralsList;
    }

    private String extractType(Type type) {
        if (type == null) {
            return null;
        }
        // Primitives and inbuilt types
        if (InBuiltTypes.getType(type.getTypeName()).isPresent()) {
            return InBuiltTypes.getType(type.getTypeName()).get();
        }
        // Types already processed
        else if (typeMap.containsKey(type.getTypeName())) {
            return type.getTypeName();
        }
        // Processes List, Set, Map
        else if (isCollection(type)) {
            return processForCollection(type);
        }
        // Can either be parametrized type
        else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return null;
        }
        // Or an enum class
        else if (((Class<?>) type).isEnum()) {
            if (enumMap.containsKey(type.getTypeName())) {
                return type.getTypeName();
            }
            UnderstandableEnum understandableEnum = new UnderstandableEnum(type.getTypeName());
            Object[] objects = ((Class<?>) type).getEnumConstants();
            Stream.of(objects).map(Object::toString).forEach(understandableEnum.getValues()::add);
            enumMap.put(understandableEnum.getName(), understandableEnum);
            return understandableEnum.getName();
        }
        // Primitives, in-built classes, and Composite objects
        else {
            UnderstandableType understandableType = new UnderstandableType(type.getTypeName());
            typeMap.put(type.getTypeName(), understandableType);
            for (Field declaredField : ((Class<?>) type).getDeclaredFields()) {
                Type fieldType = declaredField.getGenericType();
                String normalTypeName = extractType(fieldType);
                UnderstandableField understandableField = new UnderstandableField(declaredField.getName(),
                        normalTypeName);
                understandableType.getFields().add(understandableField);
            }
            return understandableType.getName();
        }
    }

    private String processForCollection(Type rawType) {
        String defaultType = InBuiltTypes.OBJECT.name();
        String defaultType2 = InBuiltTypes.OBJECT.name();
        if (rawType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) rawType;
            rawType = parameterizedType.getRawType();
            Type[] types = parameterizedType.getActualTypeArguments();
            defaultType = extractType(types[0]);
            if (types.length == 2) {
                defaultType2 = extractType(types[1]);
            }
        }

        if (List.class.isAssignableFrom((Class<?>) rawType)) {
            return "[" + defaultType + "]";
        } else if (Set.class.isAssignableFrom((Class<?>) rawType)) {
            return "<" + defaultType + ">";
        } else if (Map.class.isAssignableFrom((Class<?>) rawType)) {
            return "{" + defaultType + "," + defaultType2 + '}';
        } else {
            // Must be array type
            return "[" + extractType(((Class<?>) rawType).getComponentType()) + "]";
        }
    }

    private boolean isCollection(Type rawType) {
        if (rawType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) rawType;
            rawType = parameterizedType.getRawType();
        }
        return List.class.isAssignableFrom((Class<?>) rawType) || Set.class.isAssignableFrom((Class<?>) rawType)
                || Map.class.isAssignableFrom((Class<?>) rawType) || ((Class<?>) rawType).isArray();
    }

    public Spec build() {
        spec.setTypes(new ArrayList<>(typeMap.values()));
        spec.setEnums(new ArrayList<>(enumMap.values()));
        return spec;
    }
}