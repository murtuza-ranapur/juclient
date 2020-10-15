package com.juclient.engine.packagemanager.java;

import com.juclient.engine.Context;
import com.juclient.engine.ContextManaged;
import com.juclient.engine.client.ClientConfiguration;
import com.juclient.engine.packagemanager.Dependency;
import com.juclient.engine.packagemanager.ScaffoldCreator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Generates a maven pom file with required dependencies
 */
@Slf4j
public class MavenScaffoldCreator extends ContextManaged implements ScaffoldCreator {

    private static final String GROUP_ID = "${groupId}";
    private static final String ARTIFACT_ID = "${artifactId}";
    private static final String VERSION = "${version}";
    private static final String NAME = "${name}";
    private static final String DEPENDENCIES = "${dependencies}";
    private static final Path GENERATION_PATH = Path.of("src", "main", "java");
    private final String template;
    private final String dependencyTemplate;

    public MavenScaffoldCreator(Context context) {
        super(context);
        try {
            template = IOUtils.resourceToString("/maven/source.template", Charset.defaultCharset());
            dependencyTemplate = IOUtils.resourceToString("/maven/dependency.template", Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Maven scaffold Creator", e);
        }
    }

    @SneakyThrows(IOException.class)
    @Override
    public void create() {
        ClientConfiguration configuration = getContext().getConfiguration();
        final String projectName = configuration.getName();
        final Path generationPath = Path
                .of(StringUtils.isBlank(configuration.getTargetPath()) ? "." : configuration.getTargetPath());
        log.info("Generating maven pom file at : {}", generationPath);
        final String groupName = configuration.getGroupName();
        final String artifactName = configuration.getArtifactName();
        final String version = configuration.getVersion();
        // Generate project folder
        Path projectPath = generationPath.resolve(Paths.get(projectName));
        Files.createDirectories(projectPath);
        // Prepare pom file
        final Dependency dependency = getContext().getClientGenerator().getDependency();
        String buildTemplate = addProjectConfig(projectName, groupName, artifactName, version, template);
        buildTemplate = addDependency(dependency, buildTemplate, dependencyTemplate);
        // Write pom file
        final var pomPath = projectPath.resolve("pom.xml");
        Files.createFile(pomPath);
        // Create package structure and set generation path for type generation
        final var typeGenerationPath = projectPath.resolve(GENERATION_PATH);
        Files.createDirectories(typeGenerationPath);
        configuration.setGenerationPath(typeGenerationPath.toString());
        Files.write(pomPath, buildTemplate.getBytes(), StandardOpenOption.WRITE);

    }

    private String addDependency(Dependency dependency, String template, String dependencyTemplate) {
        dependencyTemplate = dependencyTemplate.replace(GROUP_ID, dependency.getGroupId());
        dependencyTemplate = dependencyTemplate.replace(ARTIFACT_ID, dependency.getArtifactName());
        dependencyTemplate = dependencyTemplate.replace(VERSION, dependency.getVersionName());
        template = template.replace(DEPENDENCIES, dependencyTemplate);
        return template;
    }

    private String addProjectConfig(String projectName, String groupName, String artifactName, String version,
            String template) {
        template = template.replace(NAME, projectName);
        template = template.replace(GROUP_ID, groupName);
        template = template.replace(ARTIFACT_ID, artifactName);
        template = template.replace(VERSION, version);
        return template;
    }
}
