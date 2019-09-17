package no.nav.familie.ks.sak.app.grunnlag;

import java.util.List;

public class TpsFakta {

    private PersonMedHistorikk forelder;
    private List<PersonMedHistorikk> barna;
    private PersonMedHistorikk annenForelder;

    private TpsFakta() {
    }

    public PersonMedHistorikk getForelder() {
        return forelder;
    }

    public List<PersonMedHistorikk> getBarna() {
        return barna;
    }

    @Deprecated // FIXME: legge til støtte for flerlinger i behandling
    public PersonMedHistorikk getBarn() {
        return barna.get(0);
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

        public Builder medBarn(List<PersonMedHistorikk> barna) {
            kladd.barna = barna;
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
