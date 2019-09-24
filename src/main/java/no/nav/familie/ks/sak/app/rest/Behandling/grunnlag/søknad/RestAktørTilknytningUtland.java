package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;

public class RestAktørTilknytningUtland {
    public AktørId aktør;
    public TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland;
    public String tilknytningTilUtlandForklaring;

    public RestAktørTilknytningUtland(AktørId aktør, TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland, String tilknytningTilUtlandForklaring) {
        this.aktør = aktør;
        this.tilknytningTilUtland = tilknytningTilUtland;
        this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
    }
}
