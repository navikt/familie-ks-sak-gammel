package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;

public class RestPersonrelasjon {
    public AktørId fraAktørId;
    public AktørId tilAktørId;
    public RelasjonsRolleType relasjonsrolle;
    public Boolean harSammeBosted;

    public RestPersonrelasjon(PersonRelasjon personRelasjon) {
        this.fraAktørId = personRelasjon.getFraAktørId();
        this.tilAktørId = personRelasjon.getTilAktørId();
        this.relasjonsrolle = personRelasjon.getRelasjonsrolle();
        this.harSammeBosted = personRelasjon.getHarSammeBosted();
    }
}
