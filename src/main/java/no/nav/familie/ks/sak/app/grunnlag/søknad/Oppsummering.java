package no.nav.familie.ks.sak.app.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;

public class Oppsummering {
    public String bekreftelse;

    public boolean erGyldig() {
        return Standpunkt.JA.equals(this.bekreftelse);
    }
}
