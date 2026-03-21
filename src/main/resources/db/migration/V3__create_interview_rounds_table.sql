CREATE TABLE IF NOT EXISTS interview_rounds (
    id                  BIGSERIAL PRIMARY KEY,
    round_number        INTEGER NOT NULL,
    interview_type      VARCHAR(255) NOT NULL
                            CHECK (interview_type IN ('PHONE','VIDEO','ONSITE','ASSIGNMENT','HR')),
    scheduled_at        TIMESTAMP NOT NULL,
    interviewer_name    VARCHAR(255),
    meeting_link        VARCHAR(255),
    notes               TEXT,
    outcome             VARCHAR(255),
    reminder_sent       BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP,
    job_application_id  BIGINT NOT NULL REFERENCES job_applications(id)
);