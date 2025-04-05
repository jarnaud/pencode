package com.github.jarnaud.pencode.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generate random data in the database.
 */
@Slf4j
@Component
public class Generator {

    /**
     * The dictionary of all valid words in Scrabble game.
     */
    @Value("classpath:data/scrabble.txt")
    private Resource dictionary;

    /**
     * Generate a sequence of words from a dictionary.
     *
     * @param nbWords the number of words.
     * @return the sequence of words.
     */
    public String generateWords(int nbWords) throws IOException {
        List<String> words = Files.readAllLines(dictionary.getFile().toPath());
        Random random = new Random();

        List<String> res = new ArrayList<>();
        for (int i = 0; i < nbWords; i++) {
            int index = random.nextInt(words.size());
            String word = words.get(index);
            res.add(word);
        }
        log.debug("Generated: {}", String.join(" ", res));
        return String.join(" ", res);
    }
}
