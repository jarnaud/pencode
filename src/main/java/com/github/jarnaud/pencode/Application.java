package com.github.jarnaud.pencode;

import com.github.jarnaud.pencode.extractor.Extractor;
import com.github.jarnaud.pencode.generator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Value("${pencode.generator.records}")
    private int nbRecords;

    @Value("${pencode.generator.words}")
    private int words;

    @Autowired
    private Generator generator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Extractor extractor;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).close();
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Pencode");


        //generateRecords();


        // Extract data from DB and sent to Kafka.
        extractor.extract();


        // TODO

        log.info("Exiting.");
    }

    private void generateRecords() {
        // Generate records.
        List<String> content = new ArrayList<>(nbRecords);
        for (int i = 0; i < nbRecords; i++) {
            content.add(generator.generateWords(words));
        }
        log.debug("Generated {} records.", content.size());

        // Insert into DB.
        jdbcTemplate.batchUpdate("INSERT INTO Records (content) VALUES (?)", content, 100, (ps, s) -> {
            ps.setString(1, s);
        });
    }

}
