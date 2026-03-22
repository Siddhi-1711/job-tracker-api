ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS referral_person VARCHAR(255);
ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS recruiter_name VARCHAR(255);
ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS recruiter_email VARCHAR(255);
ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS recruiter_phone VARCHAR(255);
ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS resume_version VARCHAR(100);