package com.github.jarnaud.pencode.db;

import com.github.jarnaud.pencode.model.KeyEntry;
import com.github.jarnaud.pencode.model.RecordEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Component
public class DbClient {

    private final int insertBatchSize;
    private final JdbcTemplate jdbcTemplate;

    public DbClient(JdbcTemplate jdbcTemplate,
                    @Value("${pencode.db.insert.batch}") int insertBatchSize) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertBatchSize = insertBatchSize;
    }

    private static final RowMapper<KeyEntry> KEYS_MAPPER = (rs, rowNum) -> new KeyEntry(
            rs.getLong(1),
            rs.getString(2),
            rs.getString(3)
    );

    private static final RowMapper<RecordEntry> RECORD_MAPPER = (rs, rowNum) -> new RecordEntry(
            rs.getLong(1),
            rs.getString(2),
            rs.getString(3)
    );

    // Keys.

    public void insertKeys(List<KeyPair> keyPairs) {
        jdbcTemplate.batchUpdate("INSERT INTO Keys (privateKey, publicKey) VALUES (?, ?)", keyPairs, insertBatchSize, (ps, kp) -> {
            ps.setString(1, Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()));
            ps.setString(2, Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        });
    }

    public Long getNbKeys() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Keys", Long.class);
    }

    public List<KeyEntry> getKeys() {
        return jdbcTemplate.query("SELECT * FROM Keys", KEYS_MAPPER);
    }

    // Records.

    public void insertRecords(List<String> content) {
        jdbcTemplate.batchUpdate("INSERT INTO Records (content) VALUES (?)", content, insertBatchSize, (ps, s) -> {
            ps.setString(1, s);
        });
    }

    public List<RecordEntry> getUnsignedRecords() {
        return jdbcTemplate.query("SELECT * FROM Records WHERE signature IS NULL", RECORD_MAPPER);
    }

    public void updateSignatures(Map<Long, String> signatures) {
        jdbcTemplate.batchUpdate("UPDATE Records SET signature = ? WHERE id = ?", signatures.entrySet(), insertBatchSize, (ps, en) -> {
            ps.setString(1, en.getValue());
            ps.setLong(2, en.getKey());
        });
    }

}
