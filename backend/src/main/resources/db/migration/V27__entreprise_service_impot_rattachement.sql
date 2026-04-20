ALTER TABLE entreprises
    ADD COLUMN service_impot_rattachement VARCHAR(255);

UPDATE entreprises
SET service_impot_rattachement = 'NON_CONFIGURE'
WHERE service_impot_rattachement IS NULL;

ALTER TABLE entreprises
    ALTER COLUMN service_impot_rattachement SET NOT NULL;
