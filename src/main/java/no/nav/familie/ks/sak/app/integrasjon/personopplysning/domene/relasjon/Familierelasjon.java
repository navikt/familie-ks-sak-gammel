package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;

import java.time.LocalDate;

public class Familierelasjon {
    private PersonIdent personIdent;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private Boolean harSammeBosted;

    @JsonCreator
    public Familierelasjon(@JsonProperty("personIdent") PersonIdent personIdent,
                           @JsonProperty("relasjonsrolle") RelasjonsRolleType relasjonsrolle,
                           @JsonProperty("fødselsdato") LocalDate fødselsdato,
                           @JsonProperty("harSammeBosted") Boolean harSammeBosted) {
        this.personIdent = personIdent;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.harSammeBosted = harSammeBosted;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<ident=" + personIdent
                + ", relasjon=" + relasjonsrolle
                + ", fødselsdato=" + fødselsdato
                + ", harSammeBosted=" + harSammeBosted
                + ">";
    }
}
