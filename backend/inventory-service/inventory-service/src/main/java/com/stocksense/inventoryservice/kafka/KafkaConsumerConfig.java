package com.stocksense.inventoryservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String>
    consumerFactory() {

        Map<String, Object> config =
                new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "inventory-group"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, String>
                    kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) ->
                                new TopicPartition(
                                        "inventory-events-dlt",
                                        record.partition()
                                )
                );

        FixedBackOff fixedBackOff =
                new FixedBackOff(
                        2000L,
                        3
                );

        return new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            String>
    kafkaListenerContainerFactory(

            ConsumerFactory<String, String>
                    consumerFactory,

            DefaultErrorHandler
                    errorHandler) {

        ConcurrentKafkaListenerContainerFactory<
                String,
                String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory
        );

        factory.setCommonErrorHandler(
                errorHandler
        );

        return factory;
    }
}