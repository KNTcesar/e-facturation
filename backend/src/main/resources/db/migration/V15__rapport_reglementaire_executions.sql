CREATE TABLE rapport_reglementaire_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_rapport VARCHAR(2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    acteur VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE rapport_reglementaire_executions
    ADD CONSTRAINT ck_rapport_reglementaire_type
        CHECK (type_rapport IN ('X','Z','A'));

CREATE INDEX idx_rapport_reglementaire_type_date_fin ON rapport_reglementaire_executions(type_rapport, date_fin DESC);
