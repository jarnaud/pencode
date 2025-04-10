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

@Slf4j
@Component
public class WordGenerator {

    private final int nbWords;
    private final List<String> dictionaryWords;
    private final Random random;

    /**
     * Constructor.
     *
     * @param nbWords    the number of words to generate.
     * @param dictionary the dictionary of all valid words in Scrabble game.
     */
    public WordGenerator(
            @Value("${pencode.generator.words}") int nbWords,
            @Value("classpath:data/scrabble.txt") Resource dictionary) throws IOException {
        this.nbWords = nbWords;
        this.dictionaryWords = Files.readAllLines(dictionary.getFile().toPath());
        this.random = new Random();

        log.debug("Loaded dictionary with {} words.", dictionaryWords.size());
    }

    /**
     * Generate a sequence of words from a dictionary.
     *
     * @return the sequence of words.
     */
    public String generateWords() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < nbWords; i++) {
            int index = random.nextInt(dictionaryWords.size());
            String word = dictionaryWords.get(index);
            res.add(word);
        }
        log.trace("Generated: {}", String.join(" ", res));
        return String.join(" ", res);
    }
}
