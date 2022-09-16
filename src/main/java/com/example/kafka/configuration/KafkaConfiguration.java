package com.example.kafka.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import static org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Value("${kafka.consumer-group}")
    private String consumerGroup;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        var producerProperties = kafkaProperties.buildProducerProperties();
        producerProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaProducerInterceptor.class.getName());
        var factory = new DefaultKafkaProducerFactory<String, Object>(producerProperties);
        var transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        return factory;
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
        var properties = kafkaProperties.buildConsumerProperties();
        var consumerFactory = new DefaultKafkaConsumerFactory<String, Object>(properties);
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(RECORD);
        factory.getContainerProperties().setGroupId(consumerGroup);
        return factory;
    }

    @Bean
    KafkaAdmin.NewTopics topics() {
        var topics = KafkaTopics.getTopics();
        var newTopics = topics.stream()
                .map(topic -> TopicBuilder.name(topic).partitions(1).build())
                .toArray(NewTopic[]::new);
        return new KafkaAdmin.NewTopics(newTopics);
    }
}