package com.github.jarnaud.pencode.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WordGenerator.class})
public class WordGeneratorTest {

    @Autowired
    private WordGenerator wordGenerator;

    @Test
    public void generateWords() {
        String result = wordGenerator.generateWords();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank());
        Assertions.assertEquals(5, result.split(" ").length);
    }
}
