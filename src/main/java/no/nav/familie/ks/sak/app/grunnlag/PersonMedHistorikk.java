package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;

import java.util.Objects;

public class PersonMedHistorikk {
    private Personinfo personinfo;
    private PersonhistorikkInfo personhistorikkInfo;

    private PersonMedHistorikk() {
    }

    public PersonhistorikkInfo getPersonhistorikkInfo() {
        return personhistorikkInfo;
    }

    public Personinfo getPersoninfo() {
        return personinfo;
    }

    public static final class Builder {
        private PersonMedHistorikk kladd;

        public Builder() {
            kladd = new PersonMedHistorikk();
        }


        public PersonMedHistorikk.Builder medInfo(Personinfo personinfo) {
            kladd.personinfo = personinfo;
            return this;
        }

        public PersonMedHistorikk.Builder medPersonhistorikk(PersonhistorikkInfo personhistorikkInfo) {
            kladd.personhistorikkInfo = personhistorikkInfo;
            return this;
        }

        public PersonMedHistorikk build() {
            Objects.requireNonNull(kladd.personinfo, "Må ha personinfo om søker");
            return kladd;
        }
    }
}
