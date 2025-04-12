package com.github.jarnaud.pencode.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jarnaud.pencode.model.RecordEntry;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class KafkaTopicProducer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String kafkaBootstrap;
    private final String topicName;

    public KafkaTopicProducer(@Value("${pencode.kafka.bootstrap:localhost:9090}") String kafkaBootstrap,
                              @Value("${pencode.kafka.topic.name}") String topicName) {
        this.kafkaBootstrap = kafkaBootstrap;
        this.topicName = topicName;
    }

    public void sendMessagesToTopic(List<RecordEntry> records) {
        Properties properties = loadProperties();
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try (producer) {
            for (RecordEntry rec : records) {
                String msg = createMessage(rec);
                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, msg);
                producer.send(record);
            }
            producer.flush();
        }
    }

    private String createMessage(RecordEntry rec) {
        try {
            ObjectNode node = MAPPER.createObjectNode();
            node.put("id", rec.id());
            node.put("content", rec.content());
            return MAPPER.writer().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap); // Kafka brokers addresses.
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5000);
        return props;
    }
}
