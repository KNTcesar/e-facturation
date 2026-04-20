ALTER TABLE produits
    ADD COLUMN type_article VARCHAR(10) NOT NULL DEFAULT 'BIEN',
    ADD COLUMN mode_prix_article VARCHAR(10) NOT NULL DEFAULT 'HT',
    ADD COLUMN prix_unitaire_ttc NUMERIC(18,2) NOT NULL DEFAULT 0,
    ADD COLUMN quantite_disponible NUMERIC(18,2) NOT NULL DEFAULT 0;

UPDATE produits
SET prix_unitaire_ttc = ROUND(prix_unitaire_ht * (1 + (taux_tva / 100)), 2)
WHERE prix_unitaire_ttc = 0;

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_type_article
        CHECK (type_article IN ('BIEN', 'SERVICE'));

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_mode_prix_article
        CHECK (mode_prix_article IN ('HT', 'TTC'));

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_quantite_disponible
        CHECK (quantite_disponible >= 0);
