-- Ensure logo_url columns are TEXT, not VARCHAR(255)
ALTER TABLE entreprises
    ALTER COLUMN logo_url TYPE TEXT;

ALTER TABLE factures
    ALTER COLUMN logo_url TYPE TEXT;
