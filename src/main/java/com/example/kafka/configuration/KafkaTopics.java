package com.example.kafka.configuration;

import java.util.Arrays;
import java.util.List;

public class KafkaTopics {

    public static final String INPUT_TOPIC = "input-topic-v1";
    public static final String OUTPUT_TOPIC = "output-topic-v1";
    public static final String SUMMARY_TOPIC = "summary-topic-v1";

    public static List<String> getTopics() {
        return Arrays.asList(
                INPUT_TOPIC,
                OUTPUT_TOPIC,
                SUMMARY_TOPIC
        );
    }
}
