CREATE TABLE facture_paiements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL REFERENCES factures(id),
    mode_paiement VARCHAR(30) NOT NULL,
    montant NUMERIC(18,2) NOT NULL,
    reference_paiement VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE facture_paiements
    ADD CONSTRAINT ck_facture_paiements_mode
        CHECK (mode_paiement IN ('VIREMENT','CARTE_BANCAIRE','MOBILE_MONEY','CHEQUE','ESPECES','CREDIT'));

ALTER TABLE facture_paiements
    ADD CONSTRAINT ck_facture_paiements_montant
        CHECK (montant > 0);

CREATE INDEX idx_facture_paiements_facture_id ON facture_paiements(facture_id);
