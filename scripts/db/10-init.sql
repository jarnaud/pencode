-- Records.
CREATE TABLE IF NOT EXISTS Records (
    id SERIAL PRIMARY KEY,
    content BYTEA NOT NULL
);

-- Encrypted records.
CREATE TABLE IF NOT EXISTS EncryptedRecords (
    id SERIAL PRIMARY KEY,
    key_id INT NOT NULL REFERENCES Keys(id),
    content BYTEA NOT NULL
);

-- Encryption keys.
CREATE TABLE IF NOT EXISTS Keys (
    id SERIAL PRIMARY KEY,
    content BYTEA NOT NULL
);