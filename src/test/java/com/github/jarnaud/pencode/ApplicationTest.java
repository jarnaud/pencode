package com.github.jarnaud.pencode;

import com.github.jarnaud.pencode.db.DbClient;
import com.github.jarnaud.pencode.model.RecordEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    DbClient dbClient;

    @Test
    public void endToEndTest() {
        // Command-line app will be run.

        Long nbKeys = dbClient.getNbKeys();
        Assertions.assertEquals(100, nbKeys);

        List<RecordEntry> unsigned = dbClient.getUnsignedRecords();
        Assertions.assertNotNull(unsigned);
        Assertions.assertTrue(unsigned.isEmpty(), "Some records are still unsigned");
    }
}
