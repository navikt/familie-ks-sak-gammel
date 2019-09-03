package no.nav.familie.ks.sak.app.rest.Behandling;

public class RestOppgittErklæring {
    private boolean barnetHjemmeværendeOgIkkeAdoptert;
    private boolean borSammenMedBarnet;
    private boolean ikkeAvtaltDeltBosted;
    private boolean barnINorgeNeste12Måneder;

    public RestOppgittErklæring(boolean barnetHjemmeværendeOgIkkeAdoptert, boolean borSammenMedBarnet, boolean ikkeAvtaltDeltBosted, boolean barnINorgeNeste12Måneder) {
        this.barnetHjemmeværendeOgIkkeAdoptert = barnetHjemmeværendeOgIkkeAdoptert;
        this.borSammenMedBarnet = borSammenMedBarnet;
        this.ikkeAvtaltDeltBosted = ikkeAvtaltDeltBosted;
        this.barnINorgeNeste12Måneder = barnINorgeNeste12Måneder;
    }
}
