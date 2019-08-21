DROP TABLE IF EXISTS SAK;

CREATE TABLE FAGSAK
(
    ID            serial primary key,
    SAKSNUMMER    varchar(19) not null unique,
    AKTOER_ID     VARCHAR(50) not null,
    VERSJON       bigint       DEFAULT 0,
    OPPRETTET_AV  VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV     VARCHAR(20),
    ENDRET_TID    TIMESTAMP(3)
);
create index on FAGSAK (SAKSNUMMER);
create index on FAGSAK (AKTOER_ID);

COMMENT ON COLUMN FAGSAK.saksnummer is 'Saksnummeret som saken er journalført på';
COMMENT ON COLUMN FAGSAK.AKTOER_ID is 'Søker som har stilt kravet';

CREATE TABLE BEHANDLING
(
    ID            serial primary key,
    SAK_ID        bigint references FAGSAK (id),
    VERSJON       bigint       DEFAULT 0,
    OPPRETTET_AV  VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV     VARCHAR(20),
    ENDRET_TID    TIMESTAMP(3)
);

create index on BEHANDLING (SAK_ID);

-- Søknadsgrunnlaget

CREATE TABLE SO_UTLAND
(
    ID            serial primary key,
    VERSJON       bigint       DEFAULT 0,
    OPPRETTET_AV  VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV     VARCHAR(20),
    ENDRET_TID    TIMESTAMP(3)
);

CREATE TABLE SO_AKTOER_TILKNYTNING_UTLAND
(
    ID                           serial primary key,
    UTLAND_ID                    bigint REFERENCES SO_UTLAND (ID) NOT NULL,
    AKTOER                       VARCHAR(50)                      not null,
    BODD_ELLER_JOBBET            varchar(10)                      NOT NULL DEFAULT 'UBESVART',
    BODD_ELLER_JOBBET_FORKLARING varchar(1000),
    VERSJON                      bigint                                    DEFAULT 0,
    OPPRETTET_AV                 VARCHAR(20)                               DEFAULT 'VL',
    OPPRETTET_TID                TIMESTAMP(3)                              DEFAULT localtimestamp,
    ENDRET_AV                    VARCHAR(20),
    ENDRET_TID                   TIMESTAMP(3)
);

CREATE TABLE SO_AKTOER_ARBEID_YTELSE_UTLAND
(
    ID                              serial primary key,
    UTLAND_ID                       bigint REFERENCES SO_UTLAND (ID) NOT NULL,
    AKTOER                          VARCHAR(50)                      not null,
    ARBEID_UTLAND                   varchar(10)                      NOT NULL DEFAULT 'UBESVART',
    ARBEID_UTLAND_FORKLARING        varchar(1000),
    YTELSE_UTLAND                   varchar(10)                      NOT NULL DEFAULT 'UBESVART',
    YTELSE_UTLAND_FORKLARING        varchar(1000),
    KONTANTSTOTTE_UTLAND            varchar(10)                      NOT NULL DEFAULT 'UBESVART',
    KONTANTSTOTTE_UTLAND_FORKLARING varchar(1000),
    VERSJON                         bigint                                    DEFAULT 0,
    OPPRETTET_AV                    VARCHAR(20)                               DEFAULT 'VL',
    OPPRETTET_TID                   TIMESTAMP(3)                              DEFAULT localtimestamp,
    ENDRET_AV                       VARCHAR(20),
    ENDRET_TID                      TIMESTAMP(3)
);

CREATE TABLE SO_ERKLAERING
(
    ID                      serial primary key,
    BARN_HJEMME             boolean not null,
    BOR_SAMMEN_MED_BARNET   boolean not null,
    IKKE_AVTALT_DELT_BOSTED boolean not null,
    BARN_I_NORGE            boolean not null,
    VERSJON                 bigint       DEFAULT 0,
    OPPRETTET_AV            VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID           TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV               VARCHAR(20),
    ENDRET_TID              TIMESTAMP(3)
);

COMMENT ON COLUMN SO_ERKLAERING.BARN_HJEMME IS 'Barnet er ikke adoptert, i fosterhjem eller på institusjon.';
COMMENT ON COLUMN SO_ERKLAERING.BOR_SAMMEN_MED_BARNET IS 'Søker bor i Norge sammen med barnet';
COMMENT ON COLUMN SO_ERKLAERING.IKKE_AVTALT_DELT_BOSTED IS 'Søker og annen part har ikke avtalt delt bosted.';
COMMENT ON COLUMN SO_ERKLAERING.BARN_I_NORGE IS 'Søker og barnet skal ikke oppholde seg i utlandet mer enn tre måneder de neste tolv månedene.';

CREATE TABLE SO_SOKNAD
(
    ID                    serial primary key,
    INNSENDT_TIDSPUNKT    timestamp(3)                         not null,
    OPPGITT_ERKLAERING_ID bigint REFERENCES SO_ERKLAERING (id) NOT NULL,
    OPPGITT_UTLAND_ID     bigint REFERENCES SO_UTLAND (id)     NOT NULL,
    VERSJON               bigint       DEFAULT 0,
    OPPRETTET_AV          VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID         TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV             VARCHAR(20),
    ENDRET_TID            TIMESTAMP(3)
);

create table GR_SOKNAD
(
    ID            serial primary key,
    BEHANDLING_ID bigint references BEHANDLING (id)   not null,
    SOKNAD_ID     bigint REFERENCES SO_SOKNAD (ID),
    AKTIV         boolean      default true           not null,
    VERSJON       bigint       default 0              not null,
    OPPRETTET_AV  VARCHAR(20)  default 'VL'           not null,
    OPPRETTET_TID TIMESTAMP(3) default localtimestamp not null,
    ENDRET_AV     VARCHAR(20),
    ENDRET_TID    TIMESTAMP(3)
);


create index on GR_SOKNAD (BEHANDLING_ID);
create index on GR_SOKNAD (SOKNAD_ID);

CREATE UNIQUE INDEX UIDX_GR_SOKNAD_01
    ON GR_SOKNAD
        (
         (CASE
              WHEN AKTIV = true
                  THEN BEHANDLING_ID
              ELSE NULL END),
         (CASE
              WHEN AKTIV = true
                  THEN AKTIV
              ELSE NULL END)
            );

-- Barn grunnlag

CREATE TABLE SO_BARN
(
    ID                     serial primary key,
    FAMILIEFORHOLD_ID      bigint REFERENCES SO_FAMILIEFORHOLD (ID) not null,
    AKTOER_ID              VARCHAR(50)                              not null,
    BARNEHAGE_STATUS       VARCHAR(50)                              NOT NULL,
    BARNEHAGE_ANTALL_TIMER SMALLINT,
    BARNEHAGE_DATO         date,
    BARNEHAGE_KOMMUNE      VARCHAR(50),
    VERSJON                bigint       DEFAULT 0,
    OPPRETTET_AV           VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID          TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV              VARCHAR(20),
    ENDRET_TID             TIMESTAMP(3)
);

create index on SO_BARN (AKTOER_ID);
create index on SO_BARN (FAMILIEFORHOLD_ID);

CREATE TABLE SO_FAMILIEFORHOLD
(
    ID                          serial primary key,
    BOR_BEGGE_SAMMEN_MED_BARNET boolean not null,
    VERSJON                     bigint       DEFAULT 0,
    OPPRETTET_AV                VARCHAR(20)  DEFAULT 'VL',
    OPPRETTET_TID               TIMESTAMP(3) DEFAULT localtimestamp,
    ENDRET_AV                   VARCHAR(20),
    ENDRET_TID                  TIMESTAMP(3)
);

create table GR_BARNEHAGE_BARN
(
    ID                        serial primary key,
    BEHANDLING_ID             bigint references BEHANDLING (id)   not null,
    OPPGITT_FAMILIEFORHOLD_ID bigint REFERENCES SO_FAMILIEFORHOLD (ID),
    AKTIV                     boolean      default true           not null,
    VERSJON                   bigint       default 0              not null,
    OPPRETTET_AV              VARCHAR(20)  default 'VL'           not null,
    OPPRETTET_TID             TIMESTAMP(3) default localtimestamp not null,
    ENDRET_AV                 VARCHAR(20),
    ENDRET_TID                TIMESTAMP(3)
);

create index on GR_BARNEHAGE_BARN (BEHANDLING_ID);
create index on GR_BARNEHAGE_BARN (OPPGITT_FAMILIEFORHOLD_ID);
create index on GR_BARNEHAGE_BARN (AKTIV);

CREATE UNIQUE INDEX UIDX_GR_BARN_OG_BARNEHAGE_01
    ON GR_BARNEHAGE_BARN
        (
         (CASE
              WHEN AKTIV = true
                  THEN BEHANDLING_ID
              ELSE NULL END),
         (CASE
              WHEN AKTIV = true
                  THEN AKTIV
              ELSE NULL END)
            );