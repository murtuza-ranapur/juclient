package com.juclient.engine;

import com.juclient.engine.client.ClientConfiguration;
import com.juclient.engine.client.ClientGenerator;
import com.juclient.engine.packagemanager.ScaffoldCreator;
import com.juclient.engine.type.TypeGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Context {
    private final String clientName;
    private final ClientConfiguration configuration;

    private ClientGenerator clientGenerator;
    private ScaffoldCreator scaffoldCreator;
    private TypeGenerator typeGenerator;

}
