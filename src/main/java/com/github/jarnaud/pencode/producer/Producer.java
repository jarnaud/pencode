package com.github.jarnaud.pencode.producer;

import com.github.jarnaud.pencode.db.DbClient;
import com.github.jarnaud.pencode.model.RecordEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Extract data from DB and send it to the Kafka topics.
 */
@Slf4j
@Component
public class Producer {

    private final DbClient dbClient;
    private final KafkaTopicProducer kafkaTopicProducer;

    public Producer(DbClient dbClient,
                    KafkaTopicProducer kafkaTopicProducer) {
        this.dbClient = dbClient;
        this.kafkaTopicProducer = kafkaTopicProducer;
    }

    /**
     * Load the records without a signature from the DB and send them as message to the Kafka topic.
     */
    public void extract() {
        // Load data from DB.
        List<RecordEntry> records = dbClient.getUnsignedRecords();
        if(records.isEmpty()) {
            log.debug("No record without signature");
            return;
        }
        log.debug("Retrieved {} records without a signature", records.size());
        kafkaTopicProducer.sendMessagesToTopic(records);
    }

}
