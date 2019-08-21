package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;

public class Familierelasjon {
    private AktørId aktørId;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private Boolean harSammeBosted;

    @JsonCreator
    public Familierelasjon(@JsonProperty("aktørId") AktørId aktørId,
                           @JsonProperty("relasjonsrolle") RelasjonsRolleType relasjonsrolle,
                           @JsonProperty("fødselsdato") LocalDate fødselsdato,
                           @JsonProperty("harSammeBosted") Boolean harSammeBosted) {
        this.aktørId = aktørId;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.harSammeBosted = harSammeBosted;
    }


    public AktørId getAktørId() {
        return aktørId;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    @Override
    public String toString() {
        // tar ikke med aktørId i toString så det ikke lekkeri logger etc.
        return getClass().getSimpleName()
                + "<relasjon=" + relasjonsrolle  //$NON-NLS-1$
                + ", fødselsdato=" + fødselsdato //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }
}
