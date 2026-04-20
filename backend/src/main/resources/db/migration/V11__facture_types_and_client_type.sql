ALTER TABLE clients
    ADD COLUMN type_client VARCHAR(10) NOT NULL DEFAULT 'PM';

ALTER TABLE factures
    ADD COLUMN type_facture VARCHAR(10) NOT NULL DEFAULT 'FV',
    ADD COLUMN nature_facture_avoir VARCHAR(10);

ALTER TABLE clients
    ADD CONSTRAINT ck_clients_type_client
        CHECK (type_client IN ('CC', 'PM', 'PP', 'PC'));

ALTER TABLE factures
    ADD CONSTRAINT ck_factures_type_facture
        CHECK (type_facture IN ('FV', 'FT', 'FA', 'EV', 'ET', 'EA'));

ALTER TABLE factures
    ADD CONSTRAINT ck_factures_nature_avoir
        CHECK (nature_facture_avoir IS NULL OR nature_facture_avoir IN ('COR', 'RAN', 'RAM', 'IRRR'));
