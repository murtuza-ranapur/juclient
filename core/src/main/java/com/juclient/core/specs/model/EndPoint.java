package com.juclient.core.specs.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class EndPoint {
    private String url;
    private String group;
    private String suggestedMethodName;
    private List<RequestPeripheral> requestParams;
    private List<RequestPeripheral> headers;
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
