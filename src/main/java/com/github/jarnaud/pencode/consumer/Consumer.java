package com.github.jarnaud.pencode.consumer;

import com.github.jarnaud.pencode.db.DbClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
public class Consumer {

    @Value("${pencode.kafka.bootstrap:localhost:9090}")
    private String kafkaBootstrap;

    @Value("${pencode.kafka.topic.name}")
    private String topicName;

    @Value("${pencode.consumer.batch.size}")
    private int batchSize;

    @Value("${pencode.consumer.threads}")
    private int nbThreads;

    @Autowired
    private DbClient dbClient;

    /**
     * Process messages from the Kafka topic, according to following rules:
     * - 1: Any given key in the keyring must not be used concurrently => keys assigned to workers.
     * - 2: Keys should be selected from least recently used to most recently => linked queue.
     */
    public void processMessages() {

        log.info("Processing messages");

        // TODO param for multithreading.
        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        try (executor) {

            // Build workers.
            LinkedBlockingDeque<Worker> workers = new LinkedBlockingDeque<>();
            for (int id = 0; id < dbClient.getKeys().size(); id++) {
                workers.add(new Worker(id, dbClient.getKeys().get(id), dbClient));
            }
            log.debug("Created {} workers", workers.size());

            // Receive messages.
            Properties properties = loadProperties();
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            try (consumer) {
                consumer.subscribe(List.of(topicName));

                boolean hasData = true;
                while (hasData) {

                    log.debug("polling for records...");
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10_000));
                    if (records.isEmpty()) {
                        hasData = false;
                    } else {

                        // Take the least recently used key (block if none).
                        Worker worker = workers.takeFirst();
                        log.trace("Got worker {}", worker.getId());

                        // Handle the records with the key in parallel.
                        worker.setRecords(records);
                        CompletableFuture.runAsync(worker, executor).thenAccept(result -> workers.addLast(worker));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Properties loadProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap); // Kafka brokers addresses.
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "record-signature-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //choose from earliest/latest/none

        // The consumer max poll to the batch size, therefore limiting the number of messages retrieved.
        // Note: increasing batch size to very high values may require increasing other limits.
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, batchSize);
        return props;
    }
}
