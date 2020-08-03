package com.juclient.core.specs;

import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.UnderstandableFunction;

import java.util.List;

public class SpecGenerator {

    public static Spec generate(Extractor extractor, String targetPackage) {
        List<UnderstandableFunction> functionList = extractor.extract(targetPackage);
        SpecBuilder builder = new SpecBuilder();
        functionList.forEach(builder::add);
        return builder.build();
    }
}
