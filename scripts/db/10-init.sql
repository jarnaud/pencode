-- Records.
CREATE TABLE IF NOT EXISTS Records (
    id SERIAL PRIMARY KEY,
    content varchar NOT NULL,
    signature varchar DEFAULT NULL -- base64.
);

-- Encryption keys.
CREATE TABLE IF NOT EXISTS Keys (
    id SERIAL PRIMARY KEY,
    privateKey varchar NOT NULL, -- base64.
    publicKey varchar NOT NULL -- base64.
);