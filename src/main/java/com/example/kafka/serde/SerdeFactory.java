package com.example.kafka.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.ArrayList;

public class SerdeFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> Serde<T> json(Class<T> c) {
        var valueSerde = new JsonSerde<T>();
        valueSerde.deserializer().addTrustedPackages("*");
        return valueSerde;
    }

    public static <T> Serde<ArrayList<T>> arrayList(Class<T> clazz) {
        Serializer<ArrayList<T>> arrayListSerializer = (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        Deserializer<ArrayList<T>> arrayListDeserializer = (topic, data) -> {
            try {
                return objectMapper.readValue(new String(data), new TypeReference<ArrayList<T>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        return Serdes.serdeFrom(arrayListSerializer, arrayListDeserializer);
    }
}
