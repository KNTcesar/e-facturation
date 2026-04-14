ALTER TABLE entreprises
    ADD COLUMN pays_code VARCHAR(2),
    ADD COLUMN ville VARCHAR(120),
    ADD COLUMN telephone VARCHAR(50),
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN actif BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN date_effet DATE NOT NULL DEFAULT CURRENT_DATE;

UPDATE entreprises
SET pays_code = 'BF',
    ville = 'Ouagadougou',
    actif = TRUE,
    date_effet = CURRENT_DATE
WHERE pays_code IS NULL;

ALTER TABLE entreprises
    ALTER COLUMN pays_code SET NOT NULL,
    ALTER COLUMN ville SET NOT NULL;

CREATE TABLE etablissements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entreprise_id UUID NOT NULL REFERENCES entreprises(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(120) NOT NULL,
    principal BOOLEAN NOT NULL,
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    UNIQUE (entreprise_id, code)
);

CREATE TABLE certificats_fiscaux (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entreprise_id UUID NOT NULL REFERENCES entreprises(id) ON DELETE CASCADE,
    numero_serie VARCHAR(150) NOT NULL UNIQUE,
    autorite_emission VARCHAR(255) NOT NULL,
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE NOT NULL,
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_certificat_dates CHECK (date_fin_validite >= date_debut_validite)
);

CREATE INDEX idx_entreprises_active_date_effet ON entreprises(actif, date_effet DESC);
CREATE INDEX idx_etablissements_entreprise ON etablissements(entreprise_id);
CREATE INDEX idx_certificats_entreprise ON certificats_fiscaux(entreprise_id);
