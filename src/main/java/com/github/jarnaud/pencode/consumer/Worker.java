package com.github.jarnaud.pencode.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jarnaud.pencode.db.DbClient;
import com.github.jarnaud.pencode.model.KeyEntry;
import com.github.jarnaud.pencode.model.RecordEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Workers are responsible for signing messages using the key provided to them at construction.
 * Each worker has a unique key.
 */
@Slf4j
public class Worker implements Runnable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Getter
    private final int id;
    private final KeyEntry key;
    private final PrivateKey pk;
    private final DbClient dbClient;

    @Setter
    private ConsumerRecords<String, String> records;

    public Worker(int id, KeyEntry key, DbClient dbClient) {
        this.id = id;
        this.key = key;
        this.pk = loadPrivateKey(key.privateKey());
        this.dbClient = dbClient;
    }

    @SneakyThrows
    private static PrivateKey loadPrivateKey(String privateKey) {
        byte[] bytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    @SneakyThrows
    @Override
    public void run() {

        if (records == null || records.isEmpty()) {
            log.warn("No record to process.");
            return;
        }

        log.debug("Worker with key {} will process {} records", key.id(), records.count());

        // Sign records.
        Map<Long, String> signatures = new HashMap<>();
        for (ConsumerRecord<String, String> record : records) {
            RecordEntry rec = MAPPER.readValue(record.value(), RecordEntry.class);
            String signature = Encoder.sign(rec.content(), pk);
            signatures.put(rec.id(), signature);
        }

        // Insert into DB.
        log.trace("will insert {} signatures", signatures.size());
        dbClient.updateSignatures(signatures);
    }

}
