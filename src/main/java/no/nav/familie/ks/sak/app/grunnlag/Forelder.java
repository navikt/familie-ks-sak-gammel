package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;

import java.util.Objects;

public class Forelder {
    Personinfo personinfo;
    PersonhistorikkInfo personhistorikkInfo;

    public Forelder() {
    }

    public PersonhistorikkInfo getPersonhistorikkInfo() {
        return personhistorikkInfo;
    }

    public Personinfo getPersoninfo() {
        return personinfo;
    }


    public static final class Builder {
        private Forelder kladd;

        public Builder() {
            kladd = new Forelder();
        }


        public Forelder.Builder medPersoninfo(Personinfo personinfo) {
            kladd.personinfo = personinfo;
            return this;
        }

        public Forelder.Builder medPersonhistorikkInfo(PersonhistorikkInfo personhistorikkInfo) {
            kladd.personhistorikkInfo = personhistorikkInfo;
            return this;
        }

        public Forelder build() {
            Objects.requireNonNull(kladd.personhistorikkInfo, "Må ha personhistorikk om forelder");
            Objects.requireNonNull(kladd.personinfo, "Må ha personinfo om søker");
            return kladd;
        }
    }
}
