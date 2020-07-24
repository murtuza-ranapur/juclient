package com.juclient.core.parser;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class UnderstandableRequestPeripherals {
    private String name;
    private Type type;
    private Boolean isRequired;
    private String defaultValue;
}
