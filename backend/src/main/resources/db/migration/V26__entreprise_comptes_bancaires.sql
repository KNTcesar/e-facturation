CREATE TABLE entreprise_comptes_bancaires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entreprise_id UUID NOT NULL REFERENCES entreprises(id) ON DELETE CASCADE,
    reference_compte VARCHAR(255) NOT NULL,
    banque VARCHAR(180),
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_entreprise_compte_reference UNIQUE (entreprise_id, reference_compte)
);

CREATE INDEX idx_entreprise_comptes_bancaires_entreprise_actif
    ON entreprise_comptes_bancaires(entreprise_id, actif, updated_at DESC);
