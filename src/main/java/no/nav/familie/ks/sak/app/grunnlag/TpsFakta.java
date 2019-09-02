package no.nav.familie.ks.sak.app.grunnlag;

public class TpsFakta {

    private PersonMedHistorikk forelder;
    private PersonMedHistorikk barn;
    private PersonMedHistorikk annenForelder;

    private TpsFakta() {
    }

    public PersonMedHistorikk getForelder() {
        return forelder;
    }

    public PersonMedHistorikk getBarn() {
        return barn;
    }

    public PersonMedHistorikk getAnnenForelder() {
        return annenForelder;
    }

    public static final class Builder {
        private TpsFakta kladd;

        public Builder() {
            kladd = new TpsFakta();
        }

        public Builder medForelder(PersonMedHistorikk forelder) {
            kladd.forelder = forelder;
            return this;
        }

        public Builder medBarn(PersonMedHistorikk barn) {
            kladd.barn = barn;
            return this;
        }

        public Builder medAnnenForelder(PersonMedHistorikk annenForelder) {
            kladd.annenForelder = annenForelder;
            return this;
        }

        public TpsFakta build() {
            return kladd;
        }
    }
}
