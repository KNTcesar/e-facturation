ALTER TABLE clients
    ADD COLUMN rccm VARCHAR(120);

ALTER TABLE clients
    ALTER COLUMN ifu DROP NOT NULL;

ALTER TABLE clients
    ADD CONSTRAINT uq_clients_rccm UNIQUE (rccm);
