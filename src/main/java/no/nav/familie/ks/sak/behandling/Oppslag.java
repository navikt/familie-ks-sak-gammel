package no.nav.familie.ks.sak.behandling;

import no.nav.familie.ks.sak.grunnlag.TpsFakta;

public class Oppslag {

    public TpsFakta hentTpsFakta(String aktørId) {
        return new TpsFakta.Builder().build();
    }
}
