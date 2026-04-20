ALTER TABLE certificats_fiscaux
    ADD COLUMN numero_isf VARCHAR(150);

UPDATE certificats_fiscaux
SET numero_isf = numero_serie
WHERE numero_isf IS NULL;

ALTER TABLE certificats_fiscaux
    ALTER COLUMN numero_isf SET NOT NULL;

ALTER TABLE certificats_fiscaux
    ADD CONSTRAINT uq_certificats_fiscaux_numero_isf UNIQUE (numero_isf);
