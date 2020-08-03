package com.juclient.core.specs;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class EndPoint {
    private String url;
    private String group;
    private String suggestedMethodName;
    private List<RequestPeripherals> requestParams;
    private List<RequestPeripherals> headers;
    private List<String> pathParams;
    private String requestType;
    private String requestBodyType;
    private String returnType;

    public EndPoint() {
        this.requestParams = new LinkedList<>();
        this.headers = new LinkedList<>();
        this.pathParams = new LinkedList<>();
    }
}
