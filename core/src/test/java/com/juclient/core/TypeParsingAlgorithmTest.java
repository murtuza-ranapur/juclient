package com.juclient.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TypeParsingAlgorithmTest {
    public static void main(String[] args) {
        Type returnType = SampleController.class.getMethods()[0].getGenericReturnType();
        Type parameter = SampleController.class.getMethods()[0].getGenericParameterTypes()[0];
        List<Type> types = List.of(returnType, parameter);
        for (Type type : types) {
            if(type instanceof ParameterizedType) {
                System.out.println("Para: "+type);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                System.out.println(Arrays.toString(parameterizedType.getActualTypeArguments()));
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                System.out.println(Arrays.toString(rawType.getTypeParameters()));
                Field field = rawType.getDeclaredFields()[0];
                Type fieldType = field.getGenericType();
                System.out.println(fieldType);
            }
            if(type instanceof Class){
                System.out.println("Class: "+type);
            }
        }
    }
}
