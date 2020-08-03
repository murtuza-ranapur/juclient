package com.juclient.core.specs;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Spec {
    private String version;
    private SpecConfiguration configuration;
    private List<EndPoint> endPoints;
    private List<UnderstandableType> types;
    private List<UnderstandableEnum> enums;

    Spec() {
        this.endPoints = new LinkedList<>();
        this.types = new LinkedList<>();
        this.enums = new LinkedList<>();
    }
}
