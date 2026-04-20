CREATE TABLE mcf_configuration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    host VARCHAR(120) NOT NULL,
    port INT NOT NULL,
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE mcf_configuration
    ADD CONSTRAINT ck_mcf_configuration_port
        CHECK (port >= 1 AND port <= 65535);

CREATE INDEX idx_mcf_configuration_actif_updated ON mcf_configuration(actif, updated_at DESC);
