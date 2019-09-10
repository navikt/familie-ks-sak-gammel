CREATE TABLE PO_INFORMASJON
(
    id            bigint primary key                     NOT NULL,
    versjon       bigint       DEFAULT 0                 NOT NULL,

    opprettet_av  VARCHAR(20)  DEFAULT 'VL'              NOT NULL,
    opprettet_tid TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
    endret_av     VARCHAR(20),
    endret_tid    TIMESTAMP(3),

    CONSTRAINT PK_PO_INFORMASJON PRIMARY KEY (id)
);
CREATE SEQUENCE PO_INFORMASJON_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

CREATE TABLE PO_ADRESSE
(
    id                bigint primary key,
    po_informasjon_id bigint references PO_INFORMASJON (id)  NOT NULL,
    aktoer_id         varchar(50)                            not null,
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
create index on PO_ADRESSE (po_informasjon_id);
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
    po_informasjon_id bigint references PO_INFORMASJON (id)  NOT NULL,
    relasjonsrolle    VARCHAR(100)                           NOT NULL,
    har_samme_bosted  boolean
);
CREATE SEQUENCE PO_RELASJON_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

create index on PO_RELASJON (po_informasjon_id);
create index on PO_RELASJON (fra_aktoer_id);
create index on PO_RELASJON (til_aktoer_id);

CREATE TABLE PO_PERSONOPPLYSNING
(
    id                bigint primary key                     NOT NULL,
    po_informasjon_id bigint references PO_INFORMASJON (id)  NOT NULL,
    aktoer_id         varchar(50)                            NOT NULL,
    navn              VARCHAR(100),
    foedselsdato      DATE                                   NOT NULL,
    doedsdato         DATE,
    statsborgerskap   varchar(100)                           NOT NULL,
    opprettet_av      varchar(20)  DEFAULT 'VL'              NOT NULL,
    opprettet_tid     TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
    endret_av         varchar(20),
    versjon           bigint       DEFAULT 0                 NOT NULL,
    endret_tid        TIMESTAMP(3)

);
CREATE SEQUENCE PO_PERSONOPPLYSNING_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;
create index on PO_PERSONOPPLYSNING (po_informasjon_id);
create index on PO_PERSONOPPLYSNING (aktoer_id);

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
    po_informasjon_id bigint references PO_INFORMASJON (id)  NOT NULL,
    statsborgerskap   VARCHAR(100)                           NOT NULL
);
CREATE SEQUENCE PO_STATSBORGERSKAP_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;
create index on PO_STATSBORGERSKAP (po_informasjon_id);
create index on PO_STATSBORGERSKAP (aktoer_id);

ALTER TABLE gr_personopplysninger
    ADD COLUMN registerinformasjon_id bigint REFERENCES PO_INFORMASJON (id);


