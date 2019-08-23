package no.nav.familie.ks.sak.app.grunnlag.søknad;

import java.util.ArrayList;
import java.util.List;


public class Barnehageplass {
    public String harBarnehageplass;
    public BarnehageplassVerdier barnBarnehageplassStatus;
    public String harBarnehageplassAntallTimer;
    public String harBarnehageplassDato;
    public String harBarnehageplassKommune;
    public String harSluttetIBarnehageKommune;
    public String harSluttetIBarnehageAntallTimer;
    public String harSluttetIBarnehageDato;
    public List<VedleggMetadata> harSluttetIBarnehageVedlegg;
    public String skalBegynneIBarnehageKommune;
    public String skalBegynneIBarnehageAntallTimer;
    public String skalBegynneIBarnehageDato;
    public String skalSlutteIBarnehageKommune;
    public String skalSlutteIBarnehageAntallTimer;
    public String skalSlutteIBarnehageDato;
    public List<VedleggMetadata> skalSlutteIBarnehageVedlegg;

    public Barnehageplass() {
        this.harSluttetIBarnehageVedlegg = new ArrayList<>();
        this.skalSlutteIBarnehageVedlegg = new ArrayList<>();
    }

    public enum BarnehageplassVerdier {
        garIkkeIBarnehage("Går ikke i barnehage"),
        harBarnehageplass("Har barnehageplass"),
        harSluttetIBarnehage("Har sluttet i barnehage"),
        skalBegynneIBarnehage("Skal begynne i barnehage"),
        skalSlutteIBarnehage("Skal slutte i barnehage");

        private String beskrivelse;

        BarnehageplassVerdier(String beskrivelse) {
            this.beskrivelse = beskrivelse;
        }

        public String getBeskrivelse() {
            return beskrivelse;
        }
    }
}
