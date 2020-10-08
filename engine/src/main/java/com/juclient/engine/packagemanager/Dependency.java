package com.juclient.engine.packagemanager;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Dependency {
    private final String groupId;
    private final String artifactName;
    private final String versionName;
}
