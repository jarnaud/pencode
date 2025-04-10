package com.github.jarnaud.pencode;

import com.github.jarnaud.pencode.consumer.Consumer;
import com.github.jarnaud.pencode.db.DbClient;
import com.github.jarnaud.pencode.generator.Generator;
import com.github.jarnaud.pencode.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DbClient dbClient;

    @Autowired
    private Generator generator;

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Pencode");

        if (dbClient.getNbKeys() == 0) {
            log.info("No key stored yet, generating keys.");
            generator.generateKeys();
        }

        // Generate some records in the DB.
        generator.generateRecords();
        log.info("All records generated.");

        // Extract data from DB and send to Kafka.
        producer.extract();
        log.info("All messages sent to Kafka topic.");

        consumer.processMessages();

        log.info("Exiting.");
    }

}
