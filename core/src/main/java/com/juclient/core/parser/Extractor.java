package com.juclient.core.parser;

import java.util.List;

public interface Extractor {
    List<UnderstandableFunction> extract(String packagePath);
}
