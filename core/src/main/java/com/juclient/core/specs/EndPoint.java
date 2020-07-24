package com.juclient.core.specs;

import lombok.Data;

import java.util.List;

@Data
public class EndPoint {
    private String url;
    private String suggestedMethodName;
    private List<String> headers;
    private List<String> requestType;
    private String requestBodyType;
    private String returnType;
}
