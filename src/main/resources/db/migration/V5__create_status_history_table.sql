CREATE TABLE IF NOT EXISTS application_status_history (
    id              BIGSERIAL PRIMARY KEY,
    old_status      VARCHAR(255),
    new_status      VARCHAR(255) NOT NULL,
    changed_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    note            TEXT,
    application_id  BIGINT NOT NULL REFERENCES job_applications(id)
);