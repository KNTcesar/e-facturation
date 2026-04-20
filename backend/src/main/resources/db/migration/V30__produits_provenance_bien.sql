ALTER TABLE produits
    ADD COLUMN provenance_bien VARCHAR(20);

UPDATE produits
SET provenance_bien = 'LOCAL'
WHERE type_article = 'BIEN' AND provenance_bien IS NULL;

UPDATE produits
SET provenance_bien = NULL
WHERE type_article = 'SERVICE';

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_provenance_bien
        CHECK (provenance_bien IS NULL OR provenance_bien IN ('LOCAL', 'IMPORTATION'));

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_type_vs_provenance
        CHECK (
            (type_article = 'BIEN' AND provenance_bien IS NOT NULL)
            OR (type_article = 'SERVICE' AND provenance_bien IS NULL)
        );
