package com.juclient.engine.type;

import com.juclient.core.specs.Spec;
import com.juclient.core.specs.enums.InBuiltTypes;
import com.juclient.core.specs.model.UnderstandableField;
import com.juclient.core.specs.model.UnderstandableType;
import com.juclient.engine.Context;
import com.juclient.engine.client.ClientConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaTypeGeneratorTest {
    @InjectMocks
    private JavaTypeGenerator javaTypeGenerator;

    @Mock
    private Context context;

    @Mock
    private ClientConfiguration clientConfiguration;

    @Mock
    private Spec spec;

    @Test
    public void testGenerateJavaFilesForOnlyNativeTypes() throws IOException {
        String expected = IOUtils.toString(
                JavaTypeGenerator.class.getResourceAsStream("/output/testGenerateJavaFilesForOnlyNativeTypes.txt"),
                Charset.defaultCharset());

        UnderstandableType understandableType = new UnderstandableType("SimpleChild", "com.simple");
        understandableType.setFields(List.of(
                new UnderstandableField("name", InBuiltTypes.STRING.name()),
                new UnderstandableField("isEducated", InBuiltTypes.BOOLEAN.name()),
                new UnderstandableField("birthDate",InBuiltTypes.DATE.name()),
                new UnderstandableField("height", InBuiltTypes.DOUBLE.name()),
                new UnderstandableField("weight", InBuiltTypes.FLOAT.name()),
                new UnderstandableField("age", InBuiltTypes.INTEGER.name()),
                new UnderstandableField("pin", InBuiltTypes.LONG.name()),
                new UnderstandableField("miscellaneous", InBuiltTypes.OBJECT.name()),
                new UnderstandableField("birthTime", InBuiltTypes.TIME.name())
                ));

        Path temp = Paths.get(System.getProperty("java.io.tmpdir"));
        Path path = temp.resolve("maven-test");

        var generationPath = path.resolve("src").resolve("main").resolve("java");

        if (Files.exists(generationPath)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).filter(path1 -> !path1.endsWith("java"))
                    .map(Path::toFile).forEach(File::delete);
        }

        when(spec.getTypes()).thenReturn(List.of(understandableType));
        when(clientConfiguration.getGenerationPath()).thenReturn(generationPath.toString());
        when(clientConfiguration.getPackageName()).thenReturn("com.simple");
        when(context.getConfiguration()).thenReturn(clientConfiguration);
        when(context.getSpec()).thenReturn(spec);

        javaTypeGenerator.generate();

        var generatedPath = generationPath.resolve("com").resolve("simple").resolve("modal")
                .resolve("SimpleChild.java");

        assertTrue(Files.exists(generatedPath));
        assertEquals(expected.replace("\r\n",""), Files.readString(generatedPath).strip().replace("\n",""));
    }

    @Test
    public void testGenerateJavaFilesInCaseOfMultipleTypes() throws IOException {
        String expected1 = IOUtils.toString(
                JavaTypeGenerator.class.getResourceAsStream("/output/testGenerateJavaFilesInCaseOfMultipleTypes_1.txt"),
                Charset.defaultCharset());

        String expected2 = IOUtils.toString(
                JavaTypeGenerator.class.getResourceAsStream("/output/testGenerateJavaFilesInCaseOfMultipleTypes_2.txt"),
                Charset.defaultCharset());

        UnderstandableType simpleChild = new UnderstandableType("SimpleChild", "com.simple");
        simpleChild.setFields(List.of(
                new UnderstandableField("age", InBuiltTypes.INTEGER.name()),
                new UnderstandableField("pin", InBuiltTypes.LONG.name()),
                new UnderstandableField("miscellaneous", InBuiltTypes.OBJECT.name()),
                new UnderstandableField("birthTime", InBuiltTypes.TIME.name())
        ));

        UnderstandableType simpleCousin = new UnderstandableType("SimpleCousin", "com.simple");
        simpleCousin.setFields(List.of(
                new UnderstandableField("name", InBuiltTypes.STRING.name()),
                new UnderstandableField("isEducated", InBuiltTypes.BOOLEAN.name()),
                new UnderstandableField("birthDate",InBuiltTypes.DATE.name()),
                new UnderstandableField("height", InBuiltTypes.DOUBLE.name()),
                new UnderstandableField("weight", InBuiltTypes.FLOAT.name())
        ));

        Path temp = Paths.get(System.getProperty("java.io.tmpdir"));
        Path path = temp.resolve("maven-test");

        var generationPath = path.resolve("src").resolve("main").resolve("java");

        if (Files.exists(generationPath)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).filter(path1 -> !path1.endsWith("java"))
                    .map(Path::toFile).forEach(File::delete);
        }

        when(spec.getTypes()).thenReturn(List.of(simpleChild, simpleCousin));
        when(clientConfiguration.getGenerationPath()).thenReturn(generationPath.toString());
        when(clientConfiguration.getPackageName()).thenReturn("com.simple");
        when(context.getConfiguration()).thenReturn(clientConfiguration);
        when(context.getSpec()).thenReturn(spec);

        javaTypeGenerator.generate();

        var generatedPathSimpleChild = generationPath.resolve("com").resolve("simple").resolve("modal")
                .resolve("SimpleChild.java");

        var generatedPathSimpleCousin = generationPath.resolve("com").resolve("simple").resolve("modal")
                .resolve("SimpleCousin.java");

        assertTrue(Files.exists(generatedPathSimpleChild));
        assertTrue(Files.exists(generatedPathSimpleCousin));
        assertEquals(expected1.replace("\r\n",""), Files.readString(generatedPathSimpleChild)
                .strip().replace("\n",""));
        assertEquals(expected2.replace("\r\n",""), Files.readString(generatedPathSimpleCousin)
                .strip().replace("\n",""));
    }

    @Test
    public void testGenerateJavaFilesInCaseOfCompoundClass() throws IOException {
        String expected1 = IOUtils.toString(
                JavaTypeGenerator.class.getResourceAsStream("/output/testGenerateJavaFilesInCaseOfCompoundClass_1.txt"),
                Charset.defaultCharset());

        String expected2 = IOUtils.toString(
                JavaTypeGenerator.class.getResourceAsStream("/output/testGenerateJavaFilesInCaseOfCompoundClass_2.txt"),
                Charset.defaultCharset());

        UnderstandableType simpleChild = new UnderstandableType("SimpleChild", "com.simple");
        simpleChild.setFields(List.of(
                new UnderstandableField("cousin", "SimpleCousin"),
                new UnderstandableField("age", InBuiltTypes.INTEGER.name()),
                new UnderstandableField("pin", InBuiltTypes.LONG.name()),
                new UnderstandableField("miscellaneous", InBuiltTypes.OBJECT.name()),
                new UnderstandableField("birthTime", InBuiltTypes.TIME.name())
        ));

        UnderstandableType simpleCousin = new UnderstandableType("SimpleCousin", "com.simple");
        simpleCousin.setFields(List.of(
                new UnderstandableField("name", InBuiltTypes.STRING.name()),
                new UnderstandableField("isEducated", InBuiltTypes.BOOLEAN.name()),
                new UnderstandableField("birthDate",InBuiltTypes.DATE.name()),
                new UnderstandableField("height", InBuiltTypes.DOUBLE.name()),
                new UnderstandableField("weight", InBuiltTypes.FLOAT.name())
        ));

        Path temp = Paths.get(System.getProperty("java.io.tmpdir"));
        Path path = temp.resolve("maven-test");

        var generationPath = path.resolve("src").resolve("main").resolve("java");

        if (Files.exists(generationPath)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).filter(path1 -> !path1.endsWith("java"))
                    .map(Path::toFile).forEach(File::delete);
        }

        when(spec.getTypes()).thenReturn(List.of(simpleChild, simpleCousin));
        when(clientConfiguration.getGenerationPath()).thenReturn(generationPath.toString());
        when(clientConfiguration.getPackageName()).thenReturn("com.simple");
        when(context.getConfiguration()).thenReturn(clientConfiguration);
        when(context.getSpec()).thenReturn(spec);

        javaTypeGenerator.generate();

        var generatedPathSimpleChild = generationPath.resolve("com").resolve("simple").resolve("modal")
                .resolve("SimpleChild.java");

        var generatedPathSimpleCousin = generationPath.resolve("com").resolve("simple").resolve("modal")
                .resolve("SimpleCousin.java");

        assertTrue(Files.exists(generatedPathSimpleChild));
        assertTrue(Files.exists(generatedPathSimpleCousin));
        assertEquals(expected1.replace("\r\n",""), Files.readString(generatedPathSimpleChild)
                .strip().replace("\n",""));
        assertEquals(expected2.replace("\r\n",""), Files.readString(generatedPathSimpleCousin)
                .strip().replace("\n",""));
    }
}