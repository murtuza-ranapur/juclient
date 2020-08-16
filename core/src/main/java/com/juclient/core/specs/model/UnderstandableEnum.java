package com.juclient.core.specs.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UnderstandableEnum {
    private final String name;
    private String simpleName;
    private String path;
    private List<String> values = new LinkedList<>();

    public UnderstandableEnum(String name) {
        this.name = name;
    }

    public UnderstandableEnum(String simpleName, String path) {
        this(path + "." + simpleName);
        this.simpleName = simpleName;
        this.path = path;
    }
}
