package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_AKTOER_ARBEID_YTELSE_UTLAND")
public class AktørArbeidYtelseUtland extends BaseEntitet<Long> {

    @Column(name = "aktoer", nullable = false, updatable = false)
    private String aktørId;

    @ManyToOne
    @JoinColumn(name = "UTLAND_ID")
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @Column(name = "ARBEID_UTLAND", nullable = false, updatable = false)
    private Standpunkt arbeidIUtlandet = Standpunkt.UBESVART;

    @Column(name = "ARBEID_UTLAND_FORKLARING")
    private String arbeidIUtlandetForklaring;

    @Column(name = "YTELSE_UTLAND", nullable = false, updatable = false)
    private Standpunkt ytelseIUtlandet = Standpunkt.UBESVART;

    @Column(name = "YTELSE_UTLAND_FORKLARING")
    private String ytelseIUtlandetForklaring;

    @Column(name = "KONTANTSTOTTE_UTLAND", nullable = false, updatable = false)
    private Standpunkt kontantstøtteIUtlandet = Standpunkt.UBESVART;

    @Column(name = "KONTANTSTOTTE_UTLAND_FORKLARING")
    private String kontantstøtteIUtlandetForklaring;

    AktørArbeidYtelseUtland() {
    }

    private AktørArbeidYtelseUtland(String aktørId,
                                    Standpunkt arbeidIUtlandet,
                                    String arbeidIUtlandetForklaring,
                                    Standpunkt ytelseIUtlandet,
                                    String ytelseIUtlandetForklaring,
                                    Standpunkt kontantstøtteIUtlandet,
                                    String kontantstøtteIUtlandetForklaring) {
        this.aktørId = aktørId;
        this.arbeidIUtlandet = arbeidIUtlandet;
        this.arbeidIUtlandetForklaring = arbeidIUtlandetForklaring;
        this.ytelseIUtlandet = ytelseIUtlandet;
        this.ytelseIUtlandetForklaring = ytelseIUtlandetForklaring;
        this.kontantstøtteIUtlandet = kontantstøtteIUtlandet;
        this.kontantstøtteIUtlandetForklaring = kontantstøtteIUtlandetForklaring;
    }

    public String getAktørId() {
        return aktørId;
    }

    public Standpunkt getArbeidIUtlandet() {
        return arbeidIUtlandet;
    }

    public String getArbeidIUtlandetForklaring() {
        return arbeidIUtlandetForklaring;
    }

    public Standpunkt getYtelseIUtlandet() {
        return ytelseIUtlandet;
    }

    public String getYtelseIUtlandetForklaring() {
        return ytelseIUtlandetForklaring;
    }

    public Standpunkt getKontantstøtteIUtlandet() {
        return kontantstøtteIUtlandet;
    }

    public String getKontantstøtteIUtlandetForklaring() {
        return kontantstøtteIUtlandetForklaring;
    }

    void setUtlandsTilknytning(OppgittUtlandsTilknytning utlandsTilknytning) {
        this.utlandsTilknytning = utlandsTilknytning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørArbeidYtelseUtland that = (AktørArbeidYtelseUtland) o;
        return aktørId.equals(that.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId);
    }

    public static class Builder {
        private String aktørId;
        private Standpunkt arbeidIUtlandet;
        private String arbeidIUtlandetForklaring;
        private Standpunkt ytelseIUtlandet;
        private String ytelseIUtlandetForklaring;
        private Standpunkt kontantstøtteIUtlandet;
        private String kontantstøtteIUtlandetForklaring;

        public Builder setAktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public Builder setArbeidIUtlandet(Standpunkt arbeidIUtlandet) {
            this.arbeidIUtlandet = arbeidIUtlandet;
            return this;
        }

        public Builder setArbeidIUtlandetForklaring(String arbeidIUtlandetForklaring) {
            this.arbeidIUtlandetForklaring = arbeidIUtlandetForklaring;
            return this;
        }

        public Builder setYtelseIUtlandet(Standpunkt ytelseIUtlandet) {
            this.ytelseIUtlandet = ytelseIUtlandet;
            return this;
        }

        public Builder setYtelseIUtlandetForklaring(String ytelseIUtlandetForklaring) {
            this.ytelseIUtlandetForklaring = ytelseIUtlandetForklaring;
            return this;
        }

        public Builder setKontantstøtteIUtlandet(Standpunkt kontantstøtteIUtlandet) {
            this.kontantstøtteIUtlandet = kontantstøtteIUtlandet;
            return this;
        }

        public Builder setKontantstøtteIUtlandetForklaring(String kontantstøtteIUtlandetForklaring) {
            this.kontantstøtteIUtlandetForklaring = kontantstøtteIUtlandetForklaring;
            return this;
        }

        public AktørArbeidYtelseUtland build() {
            return new AktørArbeidYtelseUtland(aktørId, arbeidIUtlandet, arbeidIUtlandetForklaring, ytelseIUtlandet, ytelseIUtlandetForklaring, kontantstøtteIUtlandet, kontantstøtteIUtlandetForklaring);
        }
    }
}
