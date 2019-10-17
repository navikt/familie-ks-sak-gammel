package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;

import java.util.Optional;

public class MedlFakta {


    Optional<MedlemskapsInfo> søker;
    Optional<MedlemskapsInfo> annenForelder;

    public Optional<MedlemskapsInfo> getSøker() {
        return søker;
    }

    public Optional<MedlemskapsInfo> getAnnenForelder() {
        return annenForelder;
    }

    public static final class Builder {
        private MedlFakta medlFakta;

        public Builder() {
            this.medlFakta = new MedlFakta();
        }

        public Builder medSøker(Optional<MedlemskapsInfo> søker) {
            medlFakta.søker = søker;
            return this;
        }

        public Builder medAnnenForelder(Optional<MedlemskapsInfo> annenForelder) {
            medlFakta.annenForelder = annenForelder;
            return this;
        }

        public MedlFakta build() {
            return medlFakta;
        }

    }
}
