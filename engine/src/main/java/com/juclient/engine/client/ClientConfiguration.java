package com.juclient.engine.client;

import lombok.Data;

@Data
public abstract class ClientConfiguration {
    private String name;
    private String targetPath;
    private String version;
    private String groupName;
    private String artifactName;
    private String generationPath;
    private String packageName;
}
