CREATE TABLE param_lignes_commentaire (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(2) NOT NULL UNIQUE,
    etiquette VARCHAR(120) NOT NULL,
    contenu_par_defaut VARCHAR(500) NOT NULL,
    actif BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE param_lignes_commentaire
    ADD CONSTRAINT ck_param_lignes_commentaire_code
        CHECK (code IN ('A','B','C','D','E','F','G','H'));

CREATE TABLE facture_commentaires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id UUID NOT NULL REFERENCES factures(id),
    code VARCHAR(2) NOT NULL,
    etiquette VARCHAR(120) NOT NULL,
    contenu VARCHAR(500) NOT NULL,
    ordre_affichage INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE facture_commentaires
    ADD CONSTRAINT ck_facture_commentaires_code
        CHECK (code IN ('A','B','C','D','E','F','G','H'));

ALTER TABLE facture_commentaires
    ADD CONSTRAINT ck_facture_commentaires_ordre
        CHECK (ordre_affichage >= 1 AND ordre_affichage <= 8);

CREATE UNIQUE INDEX uq_facture_commentaires_facture_code ON facture_commentaires(facture_id, code);
CREATE INDEX idx_facture_commentaires_facture_ordre ON facture_commentaires(facture_id, ordre_affichage);

INSERT INTO param_lignes_commentaire (code, etiquette, contenu_par_defaut, actif, created_at, updated_at) VALUES
('A', 'Réf. exo.', 'A définir', true, now(), now()),
('B', 'Référence du certificat d''exonération', 'A définir', true, now(), now()),
('C', 'Base juridique', 'A définir', true, now(), now()),
('D', 'Réservé', 'Réservé', true, now(), now()),
('E', 'Réservé', 'Réservé', true, now(), now()),
('F', 'Réservé', 'Réservé', true, now(), now()),
('G', 'Réservé', 'Réservé', true, now(), now()),
('H', 'Réservé', 'Réservé', true, now(), now());
