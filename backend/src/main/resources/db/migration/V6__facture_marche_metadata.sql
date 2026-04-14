ALTER TABLE factures
    ADD COLUMN reference_marche VARCHAR(120),
    ADD COLUMN objet_marche VARCHAR(255),
    ADD COLUMN date_marche DATE,
    ADD COLUMN date_debut_execution DATE,
    ADD COLUMN date_fin_execution DATE;

ALTER TABLE factures
    ALTER COLUMN qr_payload TYPE TEXT;
