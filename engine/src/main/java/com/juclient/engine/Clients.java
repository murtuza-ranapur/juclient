package com.juclient.engine;

import com.juclient.engine.client.ClientConfiguration;
import lombok.Builder;

@Builder
public class Clients {
    private final ClientConfiguration configuration;
}
