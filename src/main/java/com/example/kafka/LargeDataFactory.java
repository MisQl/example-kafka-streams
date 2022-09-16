package com.example.kafka;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class LargeDataFactory {

    public static String getData() {
        try {
            var resource = new ClassPathResource("large-data");
            var inputStream = resource.getInputStream();
            return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        } catch (IOException e) {
            return "error";
        }
    }
}
