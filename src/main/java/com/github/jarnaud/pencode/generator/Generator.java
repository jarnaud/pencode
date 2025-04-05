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

    private final List<String> words;
    private final Random random;

    /**
     * Constructor.
     *
     * @param dictionary the dictionary of all valid words in Scrabble game.
     */
    public Generator(@Value("classpath:data/scrabble.txt") Resource dictionary) throws IOException {
        this.words = Files.readAllLines(dictionary.getFile().toPath());
        this.random = new Random();
        log.debug("Loaded dictionary with {} words.", words.size());
    }

    /**
     * Generate a sequence of words from a dictionary.
     *
     * @param nbWords the number of words.
     * @return the sequence of words.
     */
    public String generateWords(int nbWords) {

        List<String> res = new ArrayList<>();
        for (int i = 0; i < nbWords; i++) {
            int index = random.nextInt(words.size());
            String word = words.get(index);
            res.add(word);
        }
        log.trace("Generated: {}", String.join(" ", res));
        return String.join(" ", res);
    }
}
