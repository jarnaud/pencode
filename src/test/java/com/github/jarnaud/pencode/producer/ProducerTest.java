package com.github.jarnaud.pencode.producer;

import com.github.jarnaud.pencode.db.DbClient;
import com.github.jarnaud.pencode.model.RecordEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {Producer.class, DbClient.class, KafkaTopicProducer.class})
public class ProducerTest {

    @Autowired
    private Producer producer;

    @MockitoBean
    private DbClient dbClient;

    @MockitoBean
    private KafkaTopicProducer kafkaTopicProducer;

    @Test
    public void extract_success() {
        when(dbClient.getUnsignedRecords()).thenReturn(List.of(
                new RecordEntry(101L, "aaa", null),
                new RecordEntry(102L, "bbb", null),
                new RecordEntry(103L, "ccc", null)
        ));
        producer.extract();
        verify(dbClient, times(1)).getUnsignedRecords();
        verify(kafkaTopicProducer, times(1)).sendMessagesToTopic(anyList());
    }

    @Test
    public void extract_no_unsigned() {
        when(dbClient.getUnsignedRecords()).thenReturn(List.of());
        producer.extract();
        verify(dbClient, times(1)).getUnsignedRecords();
        verify(kafkaTopicProducer, times(0)).sendMessagesToTopic(anyList());
    }
}
