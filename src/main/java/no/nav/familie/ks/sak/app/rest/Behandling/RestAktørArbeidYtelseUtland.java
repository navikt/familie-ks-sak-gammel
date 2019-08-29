package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;

public class RestAktørArbeidYtelseUtland {
    private String aktørId;
    private Standpunkt arbeidIUtlandet;
    private String arbeidIUtlandetForklaring;
    private Standpunkt ytelseIUtlandet;
    private String ytelseIUtlandetForklaring;
    private Standpunkt kontantstøtteIUtlandet;
    private String kontantstøtteIUtlandetForklaring;

    public RestAktørArbeidYtelseUtland(String aktørId, Standpunkt arbeidIUtlandet, String arbeidIUtlandetForklaring, Standpunkt ytelseIUtlandet, String ytelseIUtlandetForklaring, Standpunkt kontantstøtteIUtlandet, String kontantstøtteIUtlandetForklaring) {
        this.aktørId = aktørId;
        this.arbeidIUtlandet = arbeidIUtlandet;
        this.arbeidIUtlandetForklaring = arbeidIUtlandetForklaring;
        this.ytelseIUtlandet = ytelseIUtlandet;
        this.ytelseIUtlandetForklaring = ytelseIUtlandetForklaring;
        this.kontantstøtteIUtlandet = kontantstøtteIUtlandet;
        this.kontantstøtteIUtlandetForklaring = kontantstøtteIUtlandetForklaring;
    }
}
