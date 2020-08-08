package com.juclient.core.specs.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UnderstandableType {
    private final String name;
    private List<UnderstandableField> fields;
    private boolean isParametrized;
    private List<String> parametrizedTypeNames;

    public UnderstandableType(String name) {
        this.name = name;
        this.fields = new LinkedList<>();
        this.parametrizedTypeNames = new LinkedList<>();
    }
}
