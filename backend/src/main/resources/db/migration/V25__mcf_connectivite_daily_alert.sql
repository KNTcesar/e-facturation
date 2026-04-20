ALTER TABLE mcf_configuration
    ADD COLUMN type_connexion VARCHAR(20) NOT NULL DEFAULT 'MACHINE';

ALTER TABLE mcf_configuration
    ADD CONSTRAINT ck_mcf_configuration_type_connexion
        CHECK (type_connexion IN ('MACHINE', 'SERVICE'));

CREATE TABLE mcf_connectivite_statut (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mcf_configuration_id UUID NOT NULL REFERENCES mcf_configuration(id),
    connecte_administration BOOLEAN NOT NULL,
    date_derniere_connexion_administration TIMESTAMPTZ,
    date_derniere_verification TIMESTAMPTZ NOT NULL,
    jours_depuis_derniere_connexion INT NOT NULL,
    alerte_active BOOLEAN NOT NULL,
    message_alerte VARCHAR(500),
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_mcf_connectivite_statut_actif_updated
    ON mcf_connectivite_statut(actif, updated_at DESC);
