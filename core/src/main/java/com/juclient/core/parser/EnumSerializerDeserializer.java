package com.juclient.core.parser;

public interface EnumSerializerDeserializer<T extends Enum<?>> {
    String serialize(Enum<?> enumObject);
    Enum<?> deserialize(String value);
}
