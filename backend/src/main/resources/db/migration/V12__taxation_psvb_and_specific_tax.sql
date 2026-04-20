ALTER TABLE produits
    ADD COLUMN groupe_taxation VARCHAR(2) NOT NULL DEFAULT 'B',
    ADD COLUMN taxe_specifique_unitaire NUMERIC(18,2) NOT NULL DEFAULT 0;

ALTER TABLE lignes_facture
    ADD COLUMN groupe_taxation VARCHAR(2) NOT NULL DEFAULT 'B',
    ADD COLUMN montant_taxe_specifique NUMERIC(18,2) NOT NULL DEFAULT 0,
    ADD COLUMN base_taxable_tva NUMERIC(18,2) NOT NULL DEFAULT 0;

UPDATE lignes_facture
SET base_taxable_tva = montant_ht
WHERE base_taxable_tva = 0;

ALTER TABLE factures
    ADD COLUMN groupe_psvb VARCHAR(2),
    ADD COLUMN taux_psvb NUMERIC(5,2),
    ADD COLUMN montant_psvb NUMERIC(18,2) NOT NULL DEFAULT 0;

ALTER TABLE produits
    ADD CONSTRAINT ck_produits_groupe_taxation
        CHECK (groupe_taxation IN ('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'));

ALTER TABLE lignes_facture
    ADD CONSTRAINT ck_lignes_facture_groupe_taxation
        CHECK (groupe_taxation IN ('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'));

ALTER TABLE factures
    ADD CONSTRAINT ck_factures_groupe_psvb
        CHECK (groupe_psvb IS NULL OR groupe_psvb IN ('A','B','C','D'));
