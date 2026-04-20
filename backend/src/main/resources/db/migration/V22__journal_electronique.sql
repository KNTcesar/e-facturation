CREATE TABLE journal_electronique (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_document VARCHAR(20) NOT NULL,
    document_id UUID NOT NULL,
    reference_document VARCHAR(120) NOT NULL,
    code_secef_dgi TEXT,
    contenu_json TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE journal_electronique
    ADD CONSTRAINT ck_journal_electronique_type_document
        CHECK (type_document IN ('FACTURE', 'RAPPORT'));

CREATE INDEX idx_journal_electronique_created_at ON journal_electronique(created_at DESC);
CREATE INDEX idx_journal_electronique_document ON journal_electronique(type_document, document_id);
