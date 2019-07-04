package no.nav.familie.ks.sak.app.behandling.fastsetting;

import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;

import java.util.Objects;

public class Faktagrunnlag {

    private TpsFakta tpsFakta;
    private Søknad søknad;

    public Faktagrunnlag() {
    }

    public TpsFakta getTpsFakta() {
        return tpsFakta;
    }

    public Søknad getSøknad() {
        return søknad;
    }


    public static final class Builder {
        private Faktagrunnlag kladd;

        public Builder() {
            kladd = new Faktagrunnlag();
        }


        public Faktagrunnlag.Builder medTpsFakta(TpsFakta tpsFakta) {
            kladd.tpsFakta = tpsFakta;
            return this;
        }

        public Faktagrunnlag.Builder medSøknad(Søknad søknad) {
            kladd.søknad = søknad;
            return this;
        }

        public Faktagrunnlag build() {
            Objects.requireNonNull(kladd.søknad, "Må ha opplysninger fra fastsetting");
            Objects.requireNonNull(kladd.søknad, "Må ha opplysninger fra TPS");
            return kladd;
        }
    }

}
