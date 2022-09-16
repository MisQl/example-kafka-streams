package com.example.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static com.example.kafka.configuration.KafkaTopics.INPUT_TOPIC;

@Component
@RequiredArgsConstructor
public class ExampleProducer implements CommandLineRunner {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        var random = new Random();
        Flux.interval(Duration.of(3, ChronoUnit.SECONDS))
                .map(String::valueOf)
                .subscribe(id -> kafkaTemplate.send(INPUT_TOPIC, id, new ExampleEvent(random.nextInt(4) + 1)));
    }
}
