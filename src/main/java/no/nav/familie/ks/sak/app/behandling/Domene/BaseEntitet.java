package no.nav.familie.ks.sak.app.behandling.Domene;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;


/**
 * En basis {@link Entity} klasse som håndtere felles standarder for utformign av tabeller (eks. sporing av hvem som har
 * opprettet eller oppdatert en rad, og når).
 */
@MappedSuperclass
public class BaseEntitet implements Serializable {

    private static final String BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES = "VL";

    @Column(name = "opprettet_av", nullable = false, updatable=false)
    private String opprettetAv;

    @Column(name = "opprettet_tid", nullable = false, updatable=false)
    private LocalDateTime opprettetTidspunkt; // NOSONAR

    @Column(name = "endret_av")
    private String endretAv;

    @Column(name = "endret_tid")
    private LocalDateTime endretTidspunkt; // NOSONAR

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    @PrePersist
    protected void onCreate() {
        this.opprettetAv = finnBrukernavn();
        this.opprettetTidspunkt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        endretAv = finnBrukernavn();
        endretTidspunkt = LocalDateTime.now();
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }


    private static String finnBrukernavn() {
        String brukerident = null; // FIXME hent fra auth context
        return brukerident != null ? brukerident : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }
}