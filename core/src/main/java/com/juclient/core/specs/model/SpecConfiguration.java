package com.juclient.core.specs.model;

import lombok.Data;

import java.util.Date;

@Data
public class SpecConfiguration {
    private String baseLanguage;
    private String spec;
    private String specVersion;
    private String baseLanguageVersion;
    private Date generationDate;
}
