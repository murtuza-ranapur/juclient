package com.juclient.core.parser;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Data
public class UnderstandableFunction {
    private String className;
    private String functionName;
    private String url;
    private List<UnderstandableRequestPeripherals> requestParam = new LinkedList<>();
    private List<UnderstandableRequestPeripherals> requestHeaders = new LinkedList<>();
    private List<String> pathParams = new LinkedList<>();
    private Type requestBodyType;
    private Type requestReturnType;
    private RequestType requestType;
    private String groupName = "";
}
