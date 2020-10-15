package com.juclient.core.specs.enums;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public enum InBuiltTypes {
    STRING("STRING", String.class), INTEGER("INTEGER", int.class, Integer.class),
    FLOAT("FLOAT", float.class, Float.class), DOUBLE("DOUBLE", double.class, Double.class),
    LONG("LONG", long.class, Long.class), BOOLEAN("BOOLEAN", boolean.class, Boolean.class),
    DATE("DATE", Date.class, LocalDate.class, LocalDateTime.class),
    TIME("TIME", LocalTime.class), OBJECT("OBJECT", Object.class);

    private final String alias;
    private final Type[] types;

    private static Map<Type, String> typeMap = Collections.unmodifiableMap(createMap());

    private static Map<Type, String> createMap() {
        Map<Type, String> map = new HashMap<>();
        for (InBuiltTypes value : values()) {
            for (Type type : value.types) {
                map.put(type, value.alias);
            }
        }
        return map;
    }

    InBuiltTypes(String alias, Type... types) {
        this.alias = alias;
        this.types = types;
    }

    public static Optional<String> getType(Type nativeType) {
        if (typeMap.containsKey(nativeType)) {
            return Optional.of(typeMap.get(nativeType));
        }
        return Optional.empty();
    }

    public static Optional<Type> getType(String inBuiltType) {
        try {
            InBuiltTypes builtIn = InBuiltTypes.valueOf(inBuiltType);
            return Optional.of(builtIn.types[0]);
        } catch (IllegalArgumentException illegalArgumentException) {
            return Optional.empty();
        }
    }
}
