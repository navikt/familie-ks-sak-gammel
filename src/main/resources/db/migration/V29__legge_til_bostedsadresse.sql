CREATE TABLE PO_BOSTEDSADRESSE
(
    id                bigint primary key,
    aktoer_id         varchar(50)                            NOT NULL,
    adresselinje1     VARCHAR(40),
    adresselinje2     VARCHAR(40),
    adresselinje3     VARCHAR(40),
    adresselinje4     VARCHAR(40),
    postnummer        VARCHAR(20),
    poststed          VARCHAR(40),
    land              VARCHAR(40),

    opprettet_av      VARCHAR(20)  DEFAULT 'VL'              NOT NULL,
    opprettet_tid     TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
    endret_av         VARCHAR(20),
    endret_tid        TIMESTAMP(3),
    versjon           bigint       DEFAULT 0                 NOT NULL,

    adresse_type      VARCHAR(100)                           NOT NULL
);
create index on PO_BOSTEDSADRESSE (aktoer_id);
create index on PO_BOSTEDSADRESSE (adresse_type);


ALTER TABLE PO_PERSON
ADD COLUMN BOSTEDSADRESSE_ID bigint REFERENCES PO_BOSTEDSADRESSE (id);
