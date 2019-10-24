package no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene;

public class InfotrygdFakta {
    private AktivKontantstøtteInfo aktivKontantstøtteInfo;

    public InfotrygdFakta(AktivKontantstøtteInfo aktivKontantstøtteInfo) {
        this.aktivKontantstøtteInfo = aktivKontantstøtteInfo;
    }

    public AktivKontantstøtteInfo getAktivKontantstøtteInfo() {
        return aktivKontantstøtteInfo;
    }
}