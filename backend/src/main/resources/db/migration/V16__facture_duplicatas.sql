CREATE TABLE facture_duplicatas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL REFERENCES factures(id),
    numero_facture VARCHAR(80) NOT NULL,
    date_facture DATE NOT NULL,
    demandeur VARCHAR(120) NOT NULL,
    motif VARCHAR(255),
    snapshot_json TEXT NOT NULL,
    empreinte_snapshot VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_facture_duplicatas_facture_date ON facture_duplicatas(facture_id, created_at DESC);