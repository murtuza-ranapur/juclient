package com.juclient.core.specs.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UnderstandableEnum {
    private final String name;
    private List<String> values = new LinkedList<>();
}
