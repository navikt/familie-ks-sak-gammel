package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Periode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;

public class Familierelasjon {
    private PersonIdent personIdent;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private String adresse;
    private Boolean harSammeBosted;

    /**
     * @deprecated bruk ctor med PersonIdent
     */
    @Deprecated
    @JsonCreator
    public Familierelasjon(@JsonProperty("fnr") String fnr,
                           @JsonProperty("relasjonsrolle") RelasjonsRolleType relasjonsrolle,
                           @JsonProperty("fødselsdato") LocalDate fødselsdato,
                           @JsonProperty("adresse") String adresse,
                           @JsonProperty("harSammeBosted") Boolean harSammeBosted) {

        this(PersonIdent.fra(fnr), relasjonsrolle, fødselsdato, adresse, harSammeBosted);
    }

    public Familierelasjon(PersonIdent personIdent, RelasjonsRolleType relasjonsrolle, LocalDate fødselsdato,
                           String adresse, Boolean harSammeBosted) {
        this.personIdent = personIdent;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.adresse = adresse;
        this.harSammeBosted = harSammeBosted;
    }

    /**
     * @deprecated bruk {@link #getPersonIdent()}
     */
    @Deprecated
    public String getFnr() {
        return personIdent.getIdent();
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public String getAdresse() {
        return adresse;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    @Override
    public String toString() {
        // tar ikke med personIdent i toString så det ikke lekkeri logger etc.
        return getClass().getSimpleName()
                + "<relasjon=" + relasjonsrolle  //$NON-NLS-1$
                + ", fødselsdato=" + fødselsdato //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }
}
