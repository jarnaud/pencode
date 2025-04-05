package com.github.jarnaud.pencode.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class GeneratorTest {

    @Autowired
    private Generator generator;

    @Test
    public void generateWords() throws IOException {
        String result = generator.generateWords(3);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank());
        Assertions.assertEquals(3, result.split(" ").length);
    }
}
