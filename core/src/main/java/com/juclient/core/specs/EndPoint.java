package com.juclient.core.specs;

import lombok.Data;

@Data
public class EndPoint {
    private String url;
    private String suggestedMethodName;
    private String requestType;
    private String requestBodyType;
    private String returnType;
}
