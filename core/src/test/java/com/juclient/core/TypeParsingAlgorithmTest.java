package com.juclient.core;

import com.juclient.extra.Gender;
import com.juclient.extra.SampleController;
import com.juclient.extra.SimpleCollectionClass;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TypeParsingAlgorithmTest {
    public static void main(String[] args) {
        Type returnType = SampleController.class.getMethods()[0].getGenericReturnType();
        Type parameter = SampleController.class.getMethods()[0].getGenericParameterTypes()[0];
        List<Type> types = List.of(returnType, parameter, Gender.class);
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                System.out.println("Para: " + type);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                System.out.println(Arrays.toString(parameterizedType.getActualTypeArguments()));
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                System.out.println("Raw type :" + rawType.getName());
                System.out.println(Arrays.toString(rawType.getTypeParameters()));
                Field field = rawType.getDeclaredFields()[0];
                Type fieldType = field.getGenericType();
                System.out.println(fieldType);
                continue;
            }
            if (((Class<?>) type).isEnum()) {
                System.out.println("Enum: " + type);
                continue;
            }
            System.out.println("Class: " + type);
        }
    }
}
