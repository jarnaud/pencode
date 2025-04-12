package com.github.jarnaud.pencode.generator;

import com.github.jarnaud.pencode.db.DbClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {Generator.class, WordGenerator.class, DbClient.class})
@TestPropertySource(properties = {
        "pencode.generator.records=40",
        "pencode.generator.keys=12"
})
public class GeneratorTest {

    @Autowired
    private Generator generator;

    @MockitoSpyBean
    private WordGenerator wordGenerator;

    @MockitoBean
    private DbClient dbClient;

    @Test
    public void generateRecords_success() {
        generator.generateRecords();
        verify(wordGenerator, times(40)).generateWords();
        verify(dbClient, times(1)).insertRecords(anyList());
    }

    @Test
    public void generateKeys_success() {
        generator.generateKeys();
        verify(dbClient, times(1)).insertKeys(anyList());
    }
}
