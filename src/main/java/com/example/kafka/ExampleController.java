package com.example.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

@RestController
@RequiredArgsConstructor
public class ExampleController {

    private final StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/count/{number}")
    public Object getCount(@PathVariable String number) {
        var kafkaStreams = factoryBean.getKafkaStreams();
        if (!RUNNING.equals(kafkaStreams.state())) {
            return Map.of("state", kafkaStreams.state());
        }

        var keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType("count", QueryableStoreTypes.keyValueStore()));
        var count = keyValueStore.get(number);
        return Map.of(number, count != null ? count : 0);
    }

    @GetMapping("/aggregate/{number}")
    public Object getAggregate(@PathVariable String number) {
        var kafkaStreams = factoryBean.getKafkaStreams();
        if (!RUNNING.equals(kafkaStreams.state())) {
            return Map.of("state", kafkaStreams.state());
        }

        var keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType("aggregate", QueryableStoreTypes.keyValueStore()));
        var result = keyValueStore.get(number);

        return result != null ? Map.of(number, result) : Collections.emptyMap();
    }
}
