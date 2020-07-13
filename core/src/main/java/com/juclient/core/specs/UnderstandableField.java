package com.juclient.core.specs;

import lombok.Data;

@Data
public class UnderstandableField {
    private String name;
    private String type;
    private boolean isCoreType = true;
}
