package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.kafka.configuration.KafkaTopics.OUTPUT_TOPIC;
import static com.example.kafka.configuration.KafkaTopics.SUMMARY_TOPIC;

@Slf4j
@Component
public class ExampleListener {

    @KafkaListener(topics = OUTPUT_TOPIC)
    void outputTopic(ConsumerRecord<String, ExampleEvent> record) {
        log.info("---> RECEIVED: " + record.value());
    }

    @KafkaListener(topics = SUMMARY_TOPIC)
    void summaryTopic(ConsumerRecord<String, ExampleEvent> record) {
        log.info("---> RECEIVED SUMMARY: " + record.key() + " - " + record.value().message());
    }
}
