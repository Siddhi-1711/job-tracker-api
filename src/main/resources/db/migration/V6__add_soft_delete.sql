ALTER TABLE job_applications ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;
UPDATE job_applications SET deleted = FALSE WHERE deleted IS NULL;