package com.juclient.spec.sping.processor;

import com.jucient.spec.spring.processor.SpringSpecExtractor;
import com.juclient.core.parser.UnderstandableFunction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpringSpecExtractorTest {
    @Test
    public void extract_api_annotation_present(){
        SpringSpecExtractor springSpecExtractor = new SpringSpecExtractor();
        List<UnderstandableFunction> functions = springSpecExtractor.extract("org.test.pack");
        assertEquals(functions.size(),5);
    }
}
