CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);