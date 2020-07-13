package com.juclient.core.specs;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UnderstandableType {
    private String name;
    private List<UnderstandableField> fields;

    public UnderstandableType() {
        this.fields = new LinkedList<>();
    }
}
