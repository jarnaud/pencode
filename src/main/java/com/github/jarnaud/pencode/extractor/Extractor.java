package com.github.jarnaud.pencode.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jarnaud.pencode.model.RecordEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * Extract data from DB and send it to the Kafka topics.
 */
@Slf4j
@Component
public class Extractor {

    @Value("${pencode.kafka.bootstrap:localhost:9090}")
    private String kafkaBootstrap;

    @Value("${pencode.kafka.topic.name}")
    private String topicName;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final RowMapper<RecordEntry> RECORD_MAPPER = (rs, rowNum) -> new RecordEntry(
            rs.getLong(1),
            rs.getString(2),
            rs.getString(3)
    );

    public void extract() {

        log.debug("Starting extractor with {} and {}", kafkaBootstrap, topicName);

        // Load data from DB.
        List<RecordEntry> records = jdbcTemplate.query("SELECT * FROM Records WHERE signature IS NULL", RECORD_MAPPER);
        log.debug("Retrieved {} records", records.size());


        // Send messages to Kafka topic.
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
