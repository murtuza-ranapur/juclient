package com.juclient.core.specs;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Spec {
    private List<EndPoint> endPoints;
    private List<UnderstandableType> types;

    public Spec(){
        this.endPoints = new LinkedList<>();
        this.types = new LinkedList<>();
    }
}
