package com.juclient.core.specs;

import com.juclient.core.specs.model.EndPoint;
import com.juclient.core.specs.model.SpecConfiguration;
import com.juclient.core.specs.model.UnderstandableEnum;
import com.juclient.core.specs.model.UnderstandableType;
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
