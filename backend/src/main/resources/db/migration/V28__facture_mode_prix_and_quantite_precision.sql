ALTER TABLE factures
    ADD COLUMN mode_prix_unitaire VARCHAR(10) NOT NULL DEFAULT 'HT';

ALTER TABLE factures
    ADD CONSTRAINT ck_factures_mode_prix_unitaire
        CHECK (mode_prix_unitaire IN ('HT', 'TTC'));

ALTER TABLE lignes_facture
    ALTER COLUMN quantite TYPE NUMERIC(18,3);

ALTER TABLE produits
    ALTER COLUMN quantite_disponible TYPE NUMERIC(18,3);
