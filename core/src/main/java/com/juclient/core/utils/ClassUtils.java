package com.juclient.core.utils;

import io.github.classgraph.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ClassUtils {

    public static List<Class<?>> getClassesByAnnotation(Class<?> annotation, String packagePath) {
        log.info("Search for APIs in {} package", packagePath);
        List<Class<?>> classes = new LinkedList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packagePath).scan()) {
            for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(annotation.getName())) {
                classes.add(routeClassInfo.loadClass());
            }
            return classes;
        } catch (Exception e) {
            throw new RuntimeException("Unable to scan classes in :" + packagePath + " " + e.getMessage());
        }
    }
}
