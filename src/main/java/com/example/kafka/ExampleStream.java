package com.example.kafka;

import com.example.kafka.serde.SerdeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Instant;
import java.util.ArrayList;

import static com.example.kafka.configuration.KafkaTopics.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExampleStream {

    @Autowired
    void kafkaStream(StreamsBuilder streamsBuilder) {
        streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), SerdeFactory.json(ExampleEvent.class)))
                .peek((k, v) -> log.info("---> PROCESSING: " + v))
                .mapValues((k, v) -> new ExampleEvent(v.message() + 10))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), SerdeFactory.json(ExampleEvent.class)));
    }

    @Autowired
    void count(StreamsBuilder streamsBuilder) {
        streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), SerdeFactory.json(ExampleEvent.class)))
                .mapValues((k, v) -> v.message())
                .groupBy((k, v) -> String.valueOf(v), Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Materialized.as("count"))
                .mapValues((k, v) -> new ExampleEvent(v))
                .toStream()
                .to(SUMMARY_TOPIC, Produced.with(Serdes.String(), SerdeFactory.json(ExampleEvent.class)));
    }

    @Autowired
    void aggregate(StreamsBuilder streamsBuilder) {
        streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), new JsonSerde<>(ExampleEvent.class)))
                .mapValues((k, v) -> v.message())
                .groupBy((k, v) -> String.valueOf(v), Grouped.with(Serdes.String(), Serdes.Long()))
                .aggregate(ArrayList::new, (k, v, aggregator) -> {
                    aggregator.add(Instant.now().toString()); // processing result
                    return aggregator;
                }, Materialized.<String, ArrayList<String>, KeyValueStore<Bytes, byte[]>>as("aggregate").withValueSerde(SerdeFactory.arrayList(String.class)));
    }
}