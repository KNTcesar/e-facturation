ALTER TABLE series_facture
    ADD COLUMN exercice INT NOT NULL DEFAULT EXTRACT(YEAR FROM CURRENT_DATE)::INT;

ALTER TABLE series_facture
    DROP CONSTRAINT IF EXISTS series_facture_code_key;

ALTER TABLE series_facture
    ADD CONSTRAINT uq_series_facture_code_exercice UNIQUE (code, exercice);

ALTER TABLE factures
    ADD COLUMN exercice INT NOT NULL DEFAULT EXTRACT(YEAR FROM CURRENT_DATE)::INT,
    ADD COLUMN motif_annulation VARCHAR(500);

CREATE INDEX idx_factures_exercice ON factures(exercice);
