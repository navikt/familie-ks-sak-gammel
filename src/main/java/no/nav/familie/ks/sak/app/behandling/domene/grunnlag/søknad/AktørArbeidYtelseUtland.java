package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.familie.ks.kontrakter.søknad.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_AKTOER_ARBEID_YTELSE_UTLAND")
public class AktørArbeidYtelseUtland extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_aktoer_arbeid_ytelse_utland_seq")
    @SequenceGenerator(name = "so_aktoer_arbeid_ytelse_utland_seq")
    private Long id;

    @Column(name = "FNR")
    private String fødselsnummer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "UTLAND_ID")
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @Enumerated(EnumType.STRING)
    @Column(name = "ARBEID_UTLAND", nullable = false, updatable = false)
    private Standpunkt arbeidIUtlandet = Standpunkt.UBESVART;

    @Column(name = "ARBEID_UTLAND_FORKLARING")
    private String arbeidIUtlandetForklaring;

    @Enumerated(EnumType.STRING)
    @Column(name = "YTELSE_UTLAND", nullable = false, updatable = false)
    private Standpunkt ytelseIUtlandet = Standpunkt.UBESVART;

    @Column(name = "YTELSE_UTLAND_FORKLARING")
    private String ytelseIUtlandetForklaring;

    @Enumerated(EnumType.STRING)
    @Column(name = "KONTANTSTOTTE_UTLAND", nullable = false, updatable = false)
    private Standpunkt kontantstøtteIUtlandet = Standpunkt.UBESVART;

    @Column(name = "KONTANTSTOTTE_UTLAND_FORKLARING")
    private String kontantstøtteIUtlandetForklaring;

    AktørArbeidYtelseUtland() {
    }

    private AktørArbeidYtelseUtland(String fødselsnummer,
                                    Standpunkt arbeidIUtlandet,
                                    String arbeidIUtlandetForklaring,
                                    Standpunkt ytelseIUtlandet,
                                    String ytelseIUtlandetForklaring,
                                    Standpunkt kontantstøtteIUtlandet,
                                    String kontantstøtteIUtlandetForklaring) {
        this.fødselsnummer = fødselsnummer;
        this.arbeidIUtlandet = arbeidIUtlandet;
        this.arbeidIUtlandetForklaring = arbeidIUtlandetForklaring;
        this.ytelseIUtlandet = ytelseIUtlandet;
        this.ytelseIUtlandetForklaring = ytelseIUtlandetForklaring;
        this.kontantstøtteIUtlandet = kontantstøtteIUtlandet;
        this.kontantstøtteIUtlandetForklaring = kontantstøtteIUtlandetForklaring;
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

    public String getFødselsnummer() {
        return fødselsnummer;
    }

    public void setFødselsnummer(String fødselsnummer) {
        this.fødselsnummer = fødselsnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørArbeidYtelseUtland that = (AktørArbeidYtelseUtland) o;
        return fødselsnummer.equals(that.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fødselsnummer);
    }

    public static class Builder {
        private String fødselsnummer;
        private Standpunkt arbeidIUtlandet;
        private String arbeidIUtlandetForklaring;
        private Standpunkt ytelseIUtlandet;
        private String ytelseIUtlandetForklaring;
        private Standpunkt kontantstøtteIUtlandet = Standpunkt.UBESVART;
        private String kontantstøtteIUtlandetForklaring;

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

        public Builder setFødselsnummer(String fødselsnummer) {
            this.fødselsnummer = fødselsnummer;
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
            return new AktørArbeidYtelseUtland(fødselsnummer, arbeidIUtlandet, arbeidIUtlandetForklaring, ytelseIUtlandet, ytelseIUtlandetForklaring, kontantstøtteIUtlandet, kontantstøtteIUtlandetForklaring);
        }
    }
}
