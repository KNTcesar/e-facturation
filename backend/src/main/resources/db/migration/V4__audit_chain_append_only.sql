ALTER TABLE journal_audit
    ADD COLUMN sequence_number BIGINT,
    ADD COLUMN previous_entry_hash VARCHAR(255),
    ADD COLUMN entry_hash VARCHAR(255),
    ADD COLUMN details VARCHAR(1000);

CREATE INDEX idx_journal_audit_sequence ON journal_audit(sequence_number);

WITH ordered AS (
    SELECT id, row_number() OVER (ORDER BY created_at, id) AS seq
    FROM journal_audit
)
UPDATE journal_audit j
SET sequence_number = o.seq
FROM ordered o
WHERE j.id = o.id;
