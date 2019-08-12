package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class TilknytningTilUtland {
    public TilknytningTilUtlandVerdier annenForelderBoddEllerJobbetINorgeMinstFemAar;
    public String annenForelderBoddEllerJobbetINorgeMinstFemAarForklaring;
    public TilknytningTilUtlandVerdier boddEllerJobbetINorgeMinstFemAar;
    public String boddEllerJobbetINorgeMinstFemAarForklaring;

    public enum TilknytningTilUtlandVerdier {
        jaINorge("Ja, i Norge"),
        jaIEOS("Ja, i et EØS-land"),
        jaLeggerSammenPerioderEOS("Ja, hvis jeg legger sammen perioder i EØS-land"),
        nei("Nei"),
        Ubesvart("Ubesvart");

        private String beskrivelse;

        TilknytningTilUtlandVerdier(String beskrivelse) {
            this.beskrivelse = beskrivelse;
        }

        public String getBeskrivelse() {
            return beskrivelse;
        }
    }
}
