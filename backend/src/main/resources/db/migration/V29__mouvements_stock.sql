CREATE TABLE mouvements_stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produit_id UUID NOT NULL,
    type_mouvement VARCHAR(32) NOT NULL,
    origine_type VARCHAR(24) NOT NULL,
    quantite NUMERIC(18,3) NOT NULL,
    stock_avant NUMERIC(18,3) NOT NULL,
    stock_apres NUMERIC(18,3) NOT NULL,
    origine_reference VARCHAR(120),
    motif VARCHAR(255),
    acteur VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE mouvements_stock
    ADD CONSTRAINT fk_mouvements_stock_produit
        FOREIGN KEY (produit_id) REFERENCES produits(id);

ALTER TABLE mouvements_stock
    ADD CONSTRAINT ck_mouvements_stock_type
        CHECK (type_mouvement IN ('ENTREE_INITIALE', 'SORTIE_FACTURE'));

ALTER TABLE mouvements_stock
    ADD CONSTRAINT ck_mouvements_stock_origine
        CHECK (origine_type IN ('PRODUIT', 'FACTURE'));

ALTER TABLE mouvements_stock
    ADD CONSTRAINT ck_mouvements_stock_quantite
        CHECK (quantite > 0);

ALTER TABLE mouvements_stock
    ADD CONSTRAINT ck_mouvements_stock_non_negatif
        CHECK (stock_avant >= 0 AND stock_apres >= 0);

CREATE INDEX idx_mouvements_stock_created_at ON mouvements_stock(created_at DESC);
CREATE INDEX idx_mouvements_stock_produit_created_at ON mouvements_stock(produit_id, created_at DESC);
