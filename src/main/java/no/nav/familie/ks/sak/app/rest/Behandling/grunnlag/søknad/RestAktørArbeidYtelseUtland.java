package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;

public class RestAktørArbeidYtelseUtland {
    public AktørId aktørId;
    public Standpunkt arbeidIUtlandet;
    public String arbeidIUtlandetForklaring;
    public Standpunkt ytelseIUtlandet;
    public String ytelseIUtlandetForklaring;
    public Standpunkt kontantstøtteIUtlandet;
    public String kontantstøtteIUtlandetForklaring;

    public RestAktørArbeidYtelseUtland(AktørId aktørId, Standpunkt arbeidIUtlandet, String arbeidIUtlandetForklaring, Standpunkt ytelseIUtlandet, String ytelseIUtlandetForklaring, Standpunkt kontantstøtteIUtlandet, String kontantstøtteIUtlandetForklaring) {
        this.aktørId = aktørId;
        this.arbeidIUtlandet = arbeidIUtlandet;
        this.arbeidIUtlandetForklaring = arbeidIUtlandetForklaring;
        this.ytelseIUtlandet = ytelseIUtlandet;
        this.ytelseIUtlandetForklaring = ytelseIUtlandetForklaring;
        this.kontantstøtteIUtlandet = kontantstøtteIUtlandet;
        this.kontantstøtteIUtlandetForklaring = kontantstøtteIUtlandetForklaring;
    }
}
