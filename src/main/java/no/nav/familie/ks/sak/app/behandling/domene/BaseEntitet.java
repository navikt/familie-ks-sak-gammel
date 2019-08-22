package no.nav.familie.ks.sak.app.behandling.domene;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * En basis {@link Entity} klasse som håndtere felles standarder for utformign av tabeller (eks. sporing av hvem som har
 * opprettet eller oppdatert en rad, og når).
 */
@MappedSuperclass
public abstract class BaseEntitet<T extends Serializable> implements Serializable {

    private static final String BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES = "VL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;

    @Column(name = "opprettet_av", nullable = false, updatable = false)
    private String opprettetAv;

    @Column(name = "opprettet_tid", nullable = false, updatable = false)
    private LocalDateTime opprettetTidspunkt; // NOSONAR

    @Column(name = "endret_av")
    private String endretAv;

    @Column(name = "endret_tid")
    private LocalDateTime endretTidspunkt; // NOSONAR

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    private static String finnBrukernavn() {
        String brukerident = null; // FIXME hent fra auth context
        return brukerident != null ? brukerident : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

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

    public T getId() {
        return id;
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
}