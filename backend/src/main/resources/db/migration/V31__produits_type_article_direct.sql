-- Migration vers un type d'article direct: LOCBIE, LOCSER, IMPBIE.
-- Remplace le modele precedent BIEN/SERVICE + provenance_bien.

ALTER TABLE produits
    DROP CONSTRAINT IF EXISTS ck_produits_type_vs_provenance;

ALTER TABLE produits
    DROP CONSTRAINT IF EXISTS ck_produits_provenance_bien;

ALTER TABLE produits
    DROP CONSTRAINT IF EXISTS ck_produits_type_article;

UPDATE produits
SET type_article = CASE
    WHEN type_article = 'SERVICE' THEN 'LOCSER'
    WHEN type_article = 'BIEN' AND provenance_bien = 'IMPORTATION' THEN 'IMPBIE'
    ELSE 'LOCBIE'
END;

ALTER TABLE produits
    DROP COLUMN IF EXISTS provenance_bien;

ALTER TABLE produits
    ALTER COLUMN type_article SET DEFAULT 'LOCBIE';

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_type_article
        CHECK (type_article IN ('LOCBIE', 'LOCSER', 'IMPBIE'));
