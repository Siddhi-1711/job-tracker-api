CREATE TABLE IF NOT EXISTS job_applications (
    id                 BIGSERIAL PRIMARY KEY,
    company_name       VARCHAR(255) NOT NULL,
    job_title          VARCHAR(255) NOT NULL,
    job_url            VARCHAR(255),
    location           VARCHAR(255),
    status             VARCHAR(255) NOT NULL DEFAULT 'APPLIED'
                           CHECK (status IN ('APPLIED','SCREENING','INTERVIEW','OFFER','REJECTED','WITHDRAWN')),
    source             VARCHAR(255)
                           CHECK (source IN ('LINKEDIN','NAUKRI','REFERRAL','COMPANY_SITE','OTHER')),
    applied_date       DATE,
    follow_up_date     DATE,
    notes              TEXT,
    salary_expectation INTEGER,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    user_id            BIGINT NOT NULL REFERENCES users(id)
);