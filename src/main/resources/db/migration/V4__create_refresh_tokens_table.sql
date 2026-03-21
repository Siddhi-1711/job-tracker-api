CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    token        VARCHAR(255) NOT NULL UNIQUE,
    expiry_date  TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id      BIGINT UNIQUE REFERENCES users(id)
);