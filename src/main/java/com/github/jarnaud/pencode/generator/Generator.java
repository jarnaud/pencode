package com.github.jarnaud.pencode.generator;

import com.github.jarnaud.pencode.db.DbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generate random data in the database.
 */
@Slf4j
@Component
public class Generator {

    private final WordGenerator wordGenerator;
    private final DbClient dbClient;
    private final int nbKeys;
    private final int nbRecords;

    public Generator(WordGenerator wordGenerator,
                     DbClient dbClient,
                     @Value("${pencode.generator.records}") int nbRecords,
                     @Value("${pencode.generator.keys}") int nbKeys) {
        this.wordGenerator = wordGenerator;
        this.dbClient = dbClient;
        this.nbRecords = nbRecords;
        this.nbKeys = nbKeys;
    }

    /**
     * Generate records and insert them in the DB.
     */
    public void generateRecords() {
        List<String> content = new ArrayList<>(nbRecords);
        for (int i = 0; i < nbRecords; i++) {
            content.add(wordGenerator.generateWords());
        }
        dbClient.insertRecords(content);
        log.debug("Generated {} records.", content.size());
    }

    /**
     * Generate key pairs and insert them in the DB.
     */
    public void generateKeys() {
        // Generate records.
        List<KeyPair> pairs = new ArrayList<>(nbKeys);
        for (int i = 0; i < nbKeys; i++) {
            pairs.add(generateKeyPair());
        }
        dbClient.insertKeys(pairs);
        log.debug("Generated {} key pairs.", pairs.size());
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
