-- Records.
CREATE TABLE IF NOT EXISTS Records (
    id SERIAL PRIMARY KEY,
    content varchar NOT NULL,
    signature BYTEA DEFAULT NULL
);

-- Encryption keys.
CREATE TABLE IF NOT EXISTS Keys (
    id SERIAL PRIMARY KEY,
    content BYTEA NOT NULL
);