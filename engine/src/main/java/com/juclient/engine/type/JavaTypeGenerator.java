package com.juclient.engine.type;

import com.juclient.core.specs.Spec;
import com.juclient.core.specs.model.UnderstandableEnum;
import com.juclient.core.specs.model.UnderstandableType;
import com.juclient.engine.Context;
import com.juclient.engine.ContextManaged;
import com.juclient.engine.client.ClientConfiguration;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This class will generate model classes required for clients in '*.model'
 * package
 */
public class JavaTypeGenerator extends ContextManaged implements TypeGenerator {

    public JavaTypeGenerator(Context context) {
        super(context);
    }

    @SneakyThrows(IOException.class)
    @Override
    public void generate() {
        ClientConfiguration clientConfiguration = getContext().getConfiguration();
        final String generationPath = clientConfiguration.getGenerationPath();
        final String basePackageName = clientConfiguration.getPackageName();

        Path finalModelGenerationPath = Path.of(generationPath).resolve(basePackageName).resolve("model");

        Files.createDirectories(finalModelGenerationPath);

        Spec spec = getContext().getSpec();

        List<UnderstandableType> understandableTypes = spec.getTypes();

        writeTypes(understandableTypes, finalModelGenerationPath, basePackageName);

        List<UnderstandableEnum> understandableEnums = spec.getEnums();

        writeEnums(understandableEnums, finalModelGenerationPath, basePackageName);

    }

    private void writeTypes(List<UnderstandableType> understandableTypes,
                            Path finalModelGenerationPath,
                            String basePackageName) {

    }

    private void writeEnums(List<UnderstandableEnum> understandableEnums,
                            Path finalModelGenerationPath,
                            String basePackageName) {

    }
}
