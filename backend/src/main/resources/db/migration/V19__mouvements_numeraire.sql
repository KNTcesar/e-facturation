CREATE TABLE mouvements_numeraire (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_mouvement VARCHAR(10) NOT NULL,
    montant NUMERIC(18,2) NOT NULL,
    date_operation DATE NOT NULL,
    motif VARCHAR(255),
    acteur VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE mouvements_numeraire
    ADD CONSTRAINT ck_mouvements_numeraire_type
        CHECK (type_mouvement IN ('DEPOT', 'RETRAIT'));

ALTER TABLE mouvements_numeraire
    ADD CONSTRAINT ck_mouvements_numeraire_montant
        CHECK (montant > 0);

CREATE INDEX idx_mouvements_numeraire_date ON mouvements_numeraire(date_operation DESC, created_at DESC);