package com.juclient.engine.type;

import com.juclient.core.specs.Spec;
import com.juclient.core.specs.enums.InBuiltTypes;
import com.juclient.core.specs.model.UnderstandableEnum;
import com.juclient.core.specs.model.UnderstandableField;
import com.juclient.core.specs.model.UnderstandableType;
import com.juclient.engine.Context;
import com.juclient.engine.ContextManaged;
import com.juclient.engine.client.ClientConfiguration;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class will generate model classes required for clients in '*.model'
 * package
 */
@Slf4j
public class JavaTypeGenerator extends ContextManaged implements TypeGenerator {

    public JavaTypeGenerator(Context context) {
        super(context);
    }

    @SneakyThrows
    @Override
    public void generate() {
        ClientConfiguration clientConfiguration = getContext().getConfiguration();
        final String generationPath = clientConfiguration.getGenerationPath();
        final String basePackageName = clientConfiguration.getPackageName()+".modal";

        Path finalModelGenerationPath = Path.of(generationPath);

        Files.createDirectories(finalModelGenerationPath);
        log.info("Classes will be generated at : {}", finalModelGenerationPath);

        Spec spec = getContext().getSpec();

        List<UnderstandableType> understandableTypes = spec.getTypes();

        writeTypes(understandableTypes, finalModelGenerationPath, basePackageName);

        List<UnderstandableEnum> understandableEnums = spec.getEnums();

        writeEnums(understandableEnums, finalModelGenerationPath, basePackageName);

    }

    private void writeTypes(List<UnderstandableType> understandableTypes,
                            Path finalModelGenerationPath,
                            String basePackageName) throws IOException {
        for (UnderstandableType understandableType : understandableTypes) {
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(understandableType.getSimpleName()).addModifiers(Modifier.PUBLIC);
            for (UnderstandableField field : understandableType.getFields()) {
                Type classType = getActualType(field.getType());
                FieldSpec fieldSpec = FieldSpec.builder(classType, field.getName(), Modifier.PRIVATE)
                        .build();
                MethodSpec getMethodSpec = MethodSpec.methodBuilder(getGetterFromName(field.getName()))
                        .returns(classType)
                        .addStatement("return $L", field.getName())
                        .build();
                MethodSpec setMethodSpec = MethodSpec.methodBuilder(getSetterFromName(field.getName()))
                        .returns(void.class)
                        .addParameter(classType, field.getName())
                        .addStatement("this.$L = $L", field.getName(), field.getName())
                        .build();
                typeSpecBuilder.addField(fieldSpec);
                typeSpecBuilder.addMethod(getMethodSpec);
                typeSpecBuilder.addMethod(setMethodSpec);
            }
            TypeSpec typeSpec = typeSpecBuilder.build();

            JavaFile javaFile = JavaFile.builder(basePackageName, typeSpec).build();
            log.info("Class {} will be written at {}", understandableType.getSimpleName(), finalModelGenerationPath);
            javaFile.writeTo(finalModelGenerationPath);
        }
    }

    private Type getActualType(String type) {
        Optional<Type> optionalType = InBuiltTypes.getType(type);
        return optionalType.orElseGet(() -> fetchRecursively(type));
    }

    private Type fetchRecursively(String type) {
        return null;
    }

    private String getSetterFromName(String name) {
        return "set"+StringUtils.capitalize(name);
    }

    private String getGetterFromName(String name) {
        return "get"+StringUtils.capitalize(name);

    }

    private void writeEnums(List<UnderstandableEnum> understandableEnums,
                            Path finalModelGenerationPath,
                            String basePackageName) {
    }
}
