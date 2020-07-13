package com.juclient.core.parser;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class UnderstandableFunction {
    private String className;
    private String functionName;
    private String url;
    private Type requestBodyType;
    private Type requestReturnType;
}
