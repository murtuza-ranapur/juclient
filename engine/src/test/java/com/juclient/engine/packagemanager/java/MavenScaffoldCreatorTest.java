package com.juclient.engine.packagemanager.java;

import com.google.common.jimfs.File;
import com.google.common.jimfs.Jimfs;
import com.juclient.engine.Context;
import com.juclient.engine.client.ClientConfiguration;
import com.juclient.engine.client.ClientGenerator;
import com.juclient.engine.packagemanager.Dependency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MavenScaffoldCreatorTest {

    @InjectMocks
    private MavenScaffoldCreator mavenScaffoldCreator;

    @Mock
    private Context context;

    @Mock
    private ClientConfiguration clientConfiguration;

    @Mock
    private ClientGenerator clientGenerator;

    @Test
    public void create_maven_scaffold(){
        //Set up in memory file system
        FileSystem fileSystem = Jimfs.newFileSystem();
        Path path = fileSystem.getPath("test");

        Dependency dependency = Dependency.builder()
                .groupId("feign")
                .artifactName("feign-art")
                .versionName("version")
                .build();

        when(context.getConfiguration()).thenReturn(clientConfiguration);
        when(context.getClientGenerator()).thenReturn(clientGenerator);
        when(clientGenerator.getDependency()).thenReturn(dependency);

        when(clientConfiguration.getName()).thenReturn("mock-project");
        when(clientConfiguration.getGroupName()).thenReturn("mock-group");
        when(clientConfiguration.getArtifactName()).thenReturn("mock-artifact");
        when(clientConfiguration.getVersion()).thenReturn("1.2.0.v");
        when(clientConfiguration.getTargetPath()).thenReturn(path.toString());

        mavenScaffoldCreator.create();

        assertTrue(Files.exists(path));
        assertTrue(Files.exists(path.resolve("mock-project")));
        assertTrue(Files.exists(path.resolve("mock-project").resolve("pom.xml")));

    }
}