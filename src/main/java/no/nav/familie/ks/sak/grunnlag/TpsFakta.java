package no.nav.familie.ks.sak.grunnlag;

import java.time.LocalDate;

public class TpsFakta {

    private LocalDate barnetsFødselsdato;
    private String statsborgerskap;

    private TpsFakta() {
    }



    public LocalDate getBarnetsFødselsdato() {
        return barnetsFødselsdato;
    }


    public String getStatsborgerskap() {
        return statsborgerskap;
    }

    public static final class Builder {
        private TpsFakta kladd;

        public Builder() {
            kladd = new TpsFakta();
        }


        public Builder medBarnetsFødselsdato(LocalDate barnetsFødselsdato) {
            kladd.barnetsFødselsdato = barnetsFødselsdato;
            return this;
        }

        public Builder medStatsborgerskap(String statsborgerskap) {
            kladd.statsborgerskap = statsborgerskap;
            return this;
        }

        public TpsFakta build() {
            return kladd;
        }
    }
}
