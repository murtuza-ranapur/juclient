package com.juclient.engine.client;

import com.juclient.engine.packagemanager.Dependency;

public interface ClientGenerator {
    void generate();

    Dependency getDependency();
}
