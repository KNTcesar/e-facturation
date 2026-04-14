CREATE TABLE entreprises (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(255) NOT NULL,
    ifu VARCHAR(120) NOT NULL UNIQUE,
    rccm VARCHAR(120) NOT NULL UNIQUE,
    regime_fiscal VARCHAR(120) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
