package com.juclient.engine.client;

import lombok.Data;

@Data
public abstract class ClientConfiguration {
    private String name;
    private String targetPath;
    private String version;
}
