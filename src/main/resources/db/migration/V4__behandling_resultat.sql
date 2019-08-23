create table VILKARS_RESULTAT
(
    ID            bigint primary key,
    VERSJON       bigint       default 0              not null,
    OPPRETTET_AV  VARCHAR(20)  default 'VL'           not null,
    OPPRETTET_TID TIMESTAMP(3) default localtimestamp not null,
    ENDRET_AV     VARCHAR(20),
    ENDRET_TID    TIMESTAMP(3)
);
CREATE SEQUENCE VILKARS_RESULTAT_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

create table VILKAR_RESULTAT
(
    ID                  bigint primary key,
    VILKARS_RESULTAT_ID bigint references VILKARS_RESULTAT (id) not null,
    VILKAR              VARCHAR(50)                             NOT NULL,
    UTFALL              VARCHAR(50)                             NOT NULL,
    REGEL_INPUT         text,
    REGEL_OUTPUT        text,
    VERSJON             bigint       default 0                  not null,
    OPPRETTET_AV        VARCHAR(20)  default 'VL'               not null,
    OPPRETTET_TID       TIMESTAMP(3) default localtimestamp     not null,
    ENDRET_AV           VARCHAR(20),
    ENDRET_TID          TIMESTAMP(3)
);
CREATE SEQUENCE VILKAR_RESULTAT_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

create table BEHANDLING_RESULTAT
(
    ID                  bigint primary key,
    BEHANDLING_ID       bigint references BEHANDLING (id)   not null,
    VILKARS_RESULTAT_ID bigint references BEHANDLING (id),
    VERSJON             bigint       default 0              not null,
    OPPRETTET_AV        VARCHAR(20)  default 'VL'           not null,
    OPPRETTET_TID       TIMESTAMP(3) default localtimestamp not null,
    ENDRET_AV           VARCHAR(20),
    ENDRET_TID          TIMESTAMP(3),
    AKTIV               boolean      default true           not null
);
CREATE SEQUENCE BEHANDLING_RESULTAT_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

CREATE UNIQUE INDEX UIDX_BEHANDLING_RESULTAT_01
    ON BEHANDLING_RESULTAT
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
