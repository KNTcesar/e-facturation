-- V5__signature_horodatage_s4.sql
-- S4 - Signature & Horodatage des factures
-- Migration for invoice signing and timestamping capabilities

-- Table pour les signatures numeriques des factures
CREATE TABLE signatures_facture (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL UNIQUE,
    data_brute VARCHAR(64) NOT NULL,
    signature_base64 TEXT NOT NULL,
    algorithme VARCHAR(100) NOT NULL,
    certificat_base64 TEXT NOT NULL,
    date_signature TIMESTAMP WITH TIME ZONE NOT NULL,
    certificat_fingerprint VARCHAR(64) NOT NULL,
    verifie BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_sig_facture FOREIGN KEY (facture_id) REFERENCES factures(id) ON DELETE CASCADE
);

CREATE INDEX idx_sig_facture_id ON signatures_facture(facture_id);
CREATE INDEX idx_sig_date_signature ON signatures_facture(date_signature);
CREATE INDEX idx_sig_fingerprint ON signatures_facture(certificat_fingerprint);

-- Table pour les horodatages (timestamps) des factures
CREATE TABLE horodatages_facture (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL UNIQUE,
    hash_documented VARCHAR(64) NOT NULL,
    token_horodatage_base64 TEXT NOT NULL,
    authorite_temps VARCHAR(200) NOT NULL,
    url_verification VARCHAR(500),
    date_horodatage TIMESTAMP WITH TIME ZONE NOT NULL,
    algorithme_hash VARCHAR(50) NOT NULL,
    verifie BOOLEAN NOT NULL DEFAULT FALSE,
    tolerance_seconds BIGINT NOT NULL DEFAULT 300,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_horo_facture FOREIGN KEY (facture_id) REFERENCES factures(id) ON DELETE CASCADE
);

CREATE INDEX idx_horo_facture_id ON horodatages_facture(facture_id);
CREATE INDEX idx_horo_date_horodatage ON horodatages_facture(date_horodatage);
CREATE INDEX idx_horo_autorite ON horodatages_facture(authorite_temps);

-- Ajouter colonnes pour le statut de signature/horodatage dans la facture
ALTER TABLE factures ADD COLUMN signee BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE factures ADD COLUMN horodatee BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE factures ADD COLUMN date_signature TIMESTAMP WITH TIME ZONE;
ALTER TABLE factures ADD COLUMN date_horodatage TIMESTAMP WITH TIME ZONE;

CREATE INDEX idx_fact_signee ON factures(signee);
CREATE INDEX idx_fact_horodatee ON factures(horodatee);
