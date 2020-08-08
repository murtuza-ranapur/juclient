package com.juclient.core.specs.model;

import lombok.Data;

@Data
public class RequestPeripheral {
    private String name;
    private String type;
    private boolean isRequired;
}
