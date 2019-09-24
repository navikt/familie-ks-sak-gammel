package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

public class RestOppgittErklæring {
    public boolean barnetHjemmeværendeOgIkkeAdoptert;
    public boolean borSammenMedBarnet;
    public boolean ikkeAvtaltDeltBosted;
    public boolean barnINorgeNeste12Måneder;

    public RestOppgittErklæring(boolean barnetHjemmeværendeOgIkkeAdoptert, boolean borSammenMedBarnet, boolean ikkeAvtaltDeltBosted, boolean barnINorgeNeste12Måneder) {
        this.barnetHjemmeværendeOgIkkeAdoptert = barnetHjemmeværendeOgIkkeAdoptert;
        this.borSammenMedBarnet = borSammenMedBarnet;
        this.ikkeAvtaltDeltBosted = ikkeAvtaltDeltBosted;
        this.barnINorgeNeste12Måneder = barnINorgeNeste12Måneder;
    }
}
