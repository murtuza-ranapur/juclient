package com.juclient.core.specs;

import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripherals;
import com.juclient.core.specs.enums.InBuiltTypes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SpecBuilder {
    private Spec spec;
    private Map<String, UnderstandableType> typeMap;
    private Map<String, UnderstandableEnum> enumMap;

    public SpecBuilder() {
        this.spec = new Spec();
        this.typeMap = new HashMap<>();
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
        endPoint.setRequestParams(extractPeripehrals(understandableFunction.getRequestParam()));
        endPoint.setHeaders(extractPeripehrals(understandableFunction.getRequestHeaders()));
        spec.getEndPoints().add(endPoint);
        return this;
    }

    private List<RequestPeripherals> extractPeripehrals(List<UnderstandableRequestPeripherals> requestParam) {
        return null;
    }

    //TODO: Special handling for List, Set, and Map
    private String extractType(Type type){
        // Can either be parametrized type
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            return null;
        }
        // Or an enum class
        else if (((Class<?>) type).isEnum()) {
            if(enumMap.containsKey(type.getTypeName())){
                return type.getTypeName();
            }
            UnderstandableEnum understandableEnum = new UnderstandableEnum(type.getTypeName());
            Object[] objects = ((Class<?>) type).getEnumConstants();
            Stream.of(objects).map(Object::toString).forEach(understandableEnum.getValues()::add);
            enumMap.put(understandableEnum.getName(), understandableEnum);
            return understandableEnum.getName();
        }
        // Primitives, in-built classes, and pre-final types
        else {
            UnderstandableType understandableType = new UnderstandableType(type.getTypeName());
            typeMap.put(type.getTypeName(), understandableType);
            for (Field declaredField : type.getClass().getDeclaredFields()) {
                Type fieldType = declaredField.getGenericType();
                String normalTypeName = getTypeName(fieldType);
                UnderstandableField understandableField = new UnderstandableField(declaredField.getName(),
                        normalTypeName);
                understandableType.getFields().add(understandableField);
            }
            return understandableType.getName();
        }
    }

    private String getTypeName(Type fieldType) {
        if(typeMap.containsKey(fieldType.getTypeName())){
            return fieldType.getTypeName();
        } else if(InBuiltTypes.getType(fieldType.getTypeName()).isPresent()){
            return InBuiltTypes.getType(fieldType.getTypeName()).get();
        }else{
            return extractType(fieldType);
        }
    }

    public Spec build() {
        return spec;
    }
}