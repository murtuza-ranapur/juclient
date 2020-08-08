package com.juclient.core.specs.model;

import lombok.Data;

@Data
public class UnderstandableField {
    private final String name;
    private final String type;
    private boolean isCoreType = true;
}
