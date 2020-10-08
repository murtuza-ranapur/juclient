package com.juclient.engine.client.feign;

import com.juclient.engine.Context;
import com.juclient.engine.ContextManaged;
import com.juclient.engine.client.ClientGenerator;
import com.juclient.engine.packagemanager.Dependency;

public class FeignClientGenerator extends ContextManaged implements ClientGenerator {

    public FeignClientGenerator(Context context) {
        super(context);
    }

    @Override
    public void generate() {

    }

    @Override
    public Dependency getDependency() {
        return Dependency.builder().groupId("org.springframework.cloud").artifactName("spring-cloud-starter-openfeign")
                .versionName("2.2.4.RELEASE").build();
    }

}
