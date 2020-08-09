package com.juclient.core.specs;

import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.UnderstandableFunction;

import java.util.List;

public class SpecGenerator {
    private static final String GENERATOR_VERSION = "1.0.0";

    public static Spec generate(Extractor extractor, String targetPackage) {
        List<UnderstandableFunction> functionList = extractor.extract(targetPackage);
        SpecBuilder builder = new SpecBuilder()
                .spec(extractor.specName())
                .version(GENERATOR_VERSION);
        functionList.forEach(builder::add);
        return builder.build();
    }
}
