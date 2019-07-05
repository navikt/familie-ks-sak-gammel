package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;

public class TpsFakta {

    private Forelder forelder;
    private Personinfo barn;
    private Forelder annenForelder;

    private TpsFakta() {
    }

    public Forelder getForelder() { return forelder; }

    public Personinfo getBarn() { return barn; }

    public Forelder getAnnenForelder() { return  annenForelder; }

    public static final class Builder {
        private TpsFakta kladd;

        public Builder() {
            kladd = new TpsFakta();
        }

        public Builder medForelder(Forelder forelder) {
            kladd.forelder = forelder;
            return this;
        }

        public Builder medBarn(Personinfo barn) {
            kladd.barn = barn;
            return this;
        }

        public Builder medAnnenForelder(Forelder annenForelder) {
            kladd.annenForelder = annenForelder;
            return this;
        }

        public TpsFakta build() {
            return kladd;
        }
    }
}
