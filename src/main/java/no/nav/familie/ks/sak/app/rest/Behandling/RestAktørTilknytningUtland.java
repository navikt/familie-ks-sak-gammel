package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;

public class RestAktørTilknytningUtland {
    private String aktør;
    private TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland;
    private String tilknytningTilUtlandForklaring;

    public RestAktørTilknytningUtland(String aktør, TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland, String tilknytningTilUtlandForklaring) {
        this.aktør = aktør;
        this.tilknytningTilUtland = tilknytningTilUtland;
        this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
    }
}
