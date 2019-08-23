create table GR_PERSONOPPLYSNINGER
(
    ID                 bigint primary key,
    BEHANDLING_ID      bigint references BEHANDLING (id)   not null,
    OPPGITT_ANNEN_PART VARCHAR(50),
    VERSJON            bigint       default 0              not null,
    OPPRETTET_AV       VARCHAR(20)  default 'VL'           not null,
    OPPRETTET_TID      TIMESTAMP(3) default localtimestamp not null,
    ENDRET_AV          VARCHAR(20),
    ENDRET_TID         TIMESTAMP(3),
    AKTIV              boolean      default true           not null
);
CREATE SEQUENCE GR_PERSONOPPLYSNINGER_SEQ INCREMENT BY 50 START WITH 1000000 NO CYCLE;

CREATE UNIQUE INDEX UIDX_GR_PERSONOPPLYSNINGER_01
    ON GR_PERSONOPPLYSNINGER
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
