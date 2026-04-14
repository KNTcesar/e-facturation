CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE utilisateurs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nom_complet VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE utilisateur_roles (
    utilisateur_id UUID NOT NULL REFERENCES utilisateurs(id),
    role_id UUID NOT NULL REFERENCES roles(id),
    PRIMARY KEY (utilisateur_id, role_id)
);

CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(255) NOT NULL,
    ifu VARCHAR(120) NOT NULL UNIQUE,
    adresse VARCHAR(255) NOT NULL,
    telephone VARCHAR(50),
    email VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE produits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference VARCHAR(120) NOT NULL UNIQUE,
    designation VARCHAR(255) NOT NULL,
    prix_unitaire_ht NUMERIC(18,2) NOT NULL,
    taux_tva NUMERIC(5,2) NOT NULL,
    unite VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE series_facture (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) NOT NULL UNIQUE,
    prochain_numero BIGINT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE factures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(100) NOT NULL UNIQUE,
    date_emission DATE NOT NULL,
    statut VARCHAR(30) NOT NULL,
    total_ht NUMERIC(18,2) NOT NULL,
    total_tva NUMERIC(18,2) NOT NULL,
    total_ttc NUMERIC(18,2) NOT NULL,
    code_authentification VARCHAR(120) NOT NULL UNIQUE,
    qr_payload VARCHAR(2000) NOT NULL,
    date_certification TIMESTAMPTZ NOT NULL,
    client_id UUID NOT NULL REFERENCES clients(id),
    serie_facture_id UUID NOT NULL REFERENCES series_facture(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE lignes_facture (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL REFERENCES factures(id),
    produit_id UUID NOT NULL REFERENCES produits(id),
    description VARCHAR(255) NOT NULL,
    quantite NUMERIC(18,2) NOT NULL,
    prix_unitaire_ht NUMERIC(18,2) NOT NULL,
    taux_tva NUMERIC(5,2) NOT NULL,
    montant_ht NUMERIC(18,2) NOT NULL,
    montant_tva NUMERIC(18,2) NOT NULL,
    montant_ttc NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE transmissions_secef (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL REFERENCES factures(id),
    format_payload VARCHAR(20) NOT NULL,
    payload_hash VARCHAR(255) NOT NULL,
    statut VARCHAR(30) NOT NULL,
    code_retour VARCHAR(100),
    message_retour VARCHAR(500),
    date_envoi TIMESTAMPTZ NOT NULL,
    date_accuse TIMESTAMPTZ,
    retry_count INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE journal_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action VARCHAR(80) NOT NULL,
    entite VARCHAR(80) NOT NULL,
    entite_id UUID NOT NULL,
    old_hash VARCHAR(255),
    new_hash VARCHAR(255),
    acteur VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
