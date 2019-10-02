ALTER TABLE gr_personopplysninger
    DROP COLUMN registerinformasjon_id;

DROP SEQUENCE PO_PERSONOPPLYSNING_SEQ;
DROP SEQUENCE PO_INFORMASJON_SEQ;
DROP SEQUENCE PO_STATSBORGERSKAP_SEQ;
DROP SEQUENCE PO_ADRESSE_SEQ;
DROP SEQUENCE PO_RELASJON_SEQ;

DROP TABLE PO_INFORMASJON;
DROP TABLE PO_ADRESSE;
DROP TABLE PO_RELASJON;
DROP TABLE PO_STATSBORGERSKAP;



CREATE TABLE PO_PERSON
(
    id                          bigint primary key                            NOT NULL,
    fk_gr_personopplysninger_id bigint references gr_personopplysninger (id)  NOT NULL,
    aktoer_id                   VARCHAR(50)                                   NOT NULL,
    navn                        VARCHAR(100),
    foedselsdato                DATE                                          NOT NULL,
    doedsdato                   DATE,
    statsborgerskap             varchar(100)                                  NOT NULL,
    type                        varchar(10)                                   NOT NULL,
    opprettet_av                varchar(20)  DEFAULT 'VL'                     NOT NULL,
    opprettet_tid               TIMESTAMP(3) DEFAULT current_timestamp        NOT NULL,
    endret_av                   varchar(20),
    versjon                     bigint       DEFAULT 0                        NOT NULL,
    endret_tid                  TIMESTAMP(3)

);

CREATE SEQUENCE PO_PERSON_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;
create index on PO_PERSON (fk_gr_personopplysninger_id);
create index on PO_PERSON (aktoer_id);

CREATE TABLE PO_ADRESSE
(
    id                bigint primary key,
    fk_po_person_id   bigint references PO_PERSON (id)       NOT NULL,
    aktoer_id         varchar(50)                            NOT NULL,
    fom               DATE                                   NOT NULL,
    tom               DATE                                   NOT NULL,
    adresselinje1     VARCHAR(40)                            NOT NULL,
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
CREATE SEQUENCE PO_ADRESSE_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;
create index on PO_ADRESSE (fk_po_person_id);
create index on PO_ADRESSE (aktoer_id);
create index on PO_ADRESSE (adresse_type);

CREATE TABLE PO_RELASJON
(
    id                bigint primary key                     NOT NULL,
    fra_aktoer_id     varchar(50)                            NOT NULL,
    til_aktoer_id     varchar(50)                            NOT NULL,
    opprettet_av      VARCHAR(20)  DEFAULT 'VL'              NOT NULL,
    opprettet_tid     TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
    endret_av         VARCHAR(20),
    endret_tid        TIMESTAMP(3),
    versjon           bigint       DEFAULT 0                 NOT NULL,
    fk_po_person_id   bigint references PO_PERSON (id)       NOT NULL,
    relasjonsrolle    VARCHAR(100)                           NOT NULL,
    har_samme_bosted  boolean
);
CREATE SEQUENCE PO_RELASJON_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

create index on PO_RELASJON (fk_po_person_id);
create index on PO_RELASJON (fra_aktoer_id);
create index on PO_RELASJON (til_aktoer_id);



CREATE TABLE PO_STATSBORGERSKAP
(
    id                bigint primary key,
    aktoer_id         varchar(50)                            NOT NULL,
    fom               DATE                                   NOT NULL,
    tom               DATE                                   NOT NULL,
    opprettet_av      VARCHAR(20)  DEFAULT 'VL'              NOT NULL,
    opprettet_tid     TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
    endret_av         VARCHAR(20),
    endret_tid        TIMESTAMP(3),
    versjon           bigint       DEFAULT 0                 NOT NULL,
    fk_po_person_id   bigint references PO_PERSON (id)       NOT NULL,
    statsborgerskap   VARCHAR(100)                           NOT NULL
);
CREATE SEQUENCE PO_STATSBORGERSKAP_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;
create index on PO_STATSBORGERSKAP (fk_po_person_id);
create index on PO_STATSBORGERSKAP (aktoer_id);

ALTER TABLE gr_personopplysninger
    ADD COLUMN fk_person_id bigint REFERENCES po_person (id);


