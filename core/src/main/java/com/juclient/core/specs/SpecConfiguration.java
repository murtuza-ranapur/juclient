package com.juclient.core.specs;

import lombok.Data;

import java.util.Date;

@Data
public class SpecConfiguration {
    private String baseLanguage;
    private String baseLanguageVersion;
    private Date generationDate;
}
