package com.juclient.core.specs;

import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripheral;
import com.juclient.core.specs.enums.InBuiltTypes;
import com.juclient.core.specs.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecBuilder {
    private final Spec spec;
    private final SpecConfiguration configuration;
    private final Map<String, UnderstandableType> typeMap;
    private final Map<String, UnderstandableEnum> enumMap;

    public SpecBuilder() {
        this.spec = new Spec();
        this.configuration = new SpecConfiguration();
        this.configuration.setBaseLanguage("JAVA");
        this.configuration.setSpecVersion(Runtime.version().toString());
        this.configuration.setGenerationDate(new Date());
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

    public SpecBuilder version(String version) {
        configuration.setSpecVersion(version);
        return this;
    }

    public SpecBuilder spec(String name) {
        configuration.setSpec(name);
        return this;
    }

    private List<RequestPeripheral> extractPeripherals(List<UnderstandableRequestPeripheral> requestParam) {
        List<RequestPeripheral> peripheralsList = new ArrayList<>(requestParam.size());
        for (UnderstandableRequestPeripheral understandableRequestPeripheral : requestParam) {
            RequestPeripheral requestPeripheral = new RequestPeripheral();
            requestPeripheral.setName(understandableRequestPeripheral.getName());
            requestPeripheral.setType(extractType(understandableRequestPeripheral.getType()));
            requestPeripheral.setRequired(understandableRequestPeripheral.getIsRequired());
            peripheralsList.add(requestPeripheral);
        }
        return peripheralsList;
    }

    private String extractType(Type type) {
        if (type == null) {
            return null;
        }
        // Primitives and inbuilt types
        if (InBuiltTypes.getType(type).isPresent()) {
            return InBuiltTypes.getType(type).get();
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
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            UnderstandableType understandableType = new UnderstandableType(rawType.getSimpleName(),
                    rawType.getPackageName());
            typeMap.put(understandableType.getName(), understandableType);
            understandableType.setParametrized(true);
            understandableType.setParametrizedTypeNames(
                    Arrays.stream(rawType.getTypeParameters()).map(Objects::toString).collect(Collectors.toList()));
            for (Field declaredField : rawType.getDeclaredFields()) {
                Type fieldType = declaredField.getGenericType();
                String normalTypeName;
                if (understandableType.getParametrizedTypeNames().contains(fieldType.getTypeName())) {
                    normalTypeName = fieldType.getTypeName();
                } else {
                    normalTypeName = extractType(fieldType);
                }
                UnderstandableField understandableField = new UnderstandableField(declaredField.getName(),
                        normalTypeName);
                understandableType.getFields().add(understandableField);
            }
            String finalType = understandableType.getName() + "(";
            List<String> arguments = new ArrayList<>();
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                arguments.add(extractType(typeArgument));
            }
            finalType += String.join(",", arguments);
            finalType += ")";
            return finalType;
        }
        // Or an enum class
        else if (((Class<?>) type).isEnum()) {
            if (enumMap.containsKey(type.getTypeName())) {
                return type.getTypeName();
            }
            UnderstandableEnum understandableEnum = new UnderstandableEnum(((Class<?>) type).getSimpleName(),
                    ((Class<?>) type).getPackageName());
            Object[] objects = ((Class<?>) type).getEnumConstants();
            Stream.of(objects).map(Object::toString).forEach(understandableEnum.getValues()::add);
            enumMap.put(understandableEnum.getName(), understandableEnum);
            return understandableEnum.getName();
        }
        // Primitives, in-built classes, and Composite objects
        else {
            UnderstandableType understandableType = new UnderstandableType(((Class<?>) type).getSimpleName(),
                    ((Class<?>) type).getPackageName());
            typeMap.put(type.getTypeName(), understandableType);
            for (Field declaredField : ((Class<?>) type).getDeclaredFields()) {
                if (Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }
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
        spec.setConfiguration(configuration);
        spec.setTypes(new ArrayList<>(typeMap.values()));
        spec.setEnums(new ArrayList<>(enumMap.values()));
        return spec;
    }
}