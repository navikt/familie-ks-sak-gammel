package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.Ident;
import no.nav.familie.ks.sak.app.behandling.domene.typer.IdentType;

import java.time.LocalDate;
import java.util.Map;

public class Familierelasjon {
    private Map<IdentType, Ident> ident;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private Boolean harSammeBosted;

    @JsonCreator
    public Familierelasjon(@JsonProperty("ident") Map<IdentType, Ident> ident,
                           @JsonProperty("relasjonsrolle") RelasjonsRolleType relasjonsrolle,
                           @JsonProperty("fødselsdato") LocalDate fødselsdato,
                           @JsonProperty("harSammeBosted") Boolean harSammeBosted) {
        this.ident = ident;
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

    public Map<IdentType, Ident> getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<ident=" + ident
                + ", relasjon=" + relasjonsrolle
                + ", fødselsdato=" + fødselsdato
                + ", harSammeBosted=" + harSammeBosted
                + ">";
    }
}
