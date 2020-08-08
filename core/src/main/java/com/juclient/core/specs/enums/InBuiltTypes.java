package com.juclient.core.specs.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public enum InBuiltTypes {
    STRING("STRING", String.class.getName()), INTEGER("INTEGER", "int", Integer.class.getName()),
    FLOAT("FLOAT", "float", Float.class.getName()), DOUBLE("DOUBLE", "double", Double.class.getName()),
    LONG("LONG", "long", Long.class.getName()), BOOLEAN("BOOLEAN", "boolean", Boolean.class.getName()),
    DATE("DATE", Date.class.getName(), LocalDate.class.getName(), LocalDateTime.class.getName()),
    TIME("TIME", LocalTime.class.getName()), OBJECT("OBJECT", Object.class.getName());

    private final String alias;
    private final String[] types;

    private static Map<String, String> typeMap = Collections.unmodifiableMap(createMap());

    private static Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        for (InBuiltTypes value : values()) {
            for (String type : value.types) {
                map.put(type, value.alias);
            }
        }
        return map;
    }

    InBuiltTypes(String alias, String... types) {
        this.alias = alias;
        this.types = types;
    }

    public static Optional<String> getType(String nativeType) {
        if (typeMap.containsKey(nativeType)) {
            return Optional.of(typeMap.get(nativeType));
        }
        return Optional.empty();
    }
}
