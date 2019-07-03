package no.nav.familie.ks.sak.grunnlag;

import java.time.LocalDate;
import java.util.Objects;

public class Søknad {

    private LocalDate barnehageplassFom;
    private LocalDate barnehageplassTom;
    private int barnehageplassProsent;
    private Boolean ikkeUtlandTreMåneder;

    public Søknad() {
    }

    public int getBarnehageplassProsent() {
        return barnehageplassProsent;
    }

    public LocalDate getBarnehageplassFom() {
        return barnehageplassFom;
    }

    public LocalDate getBarnehageplassTom() {
        return barnehageplassTom;
    }

    public Boolean getIkkeUtlandTreMåneder() { return ikkeUtlandTreMåneder; }


    public static final class Builder {
        private Søknad kladd;

        public Builder() {
            kladd = new Søknad();
        }


        public Søknad.Builder medBarnehageplassFom(LocalDate barnehageplassFom) {
            kladd.barnehageplassFom = barnehageplassFom;
            return this;
        }

        public Søknad.Builder medBarnehageplassTom(LocalDate barnehageplassTom) {
            kladd.barnehageplassTom = barnehageplassTom;
            return this;
        }

        public Søknad.Builder medBarnehageplassProsent(int barnehageplassProsent) {
            kladd.barnehageplassProsent = barnehageplassProsent;
            return this;
        }


        public Søknad.Builder medIkkeUtlandTreMåneder(Boolean ikkeUtlandTreMåneder) {
            kladd.ikkeUtlandTreMåneder = ikkeUtlandTreMåneder;
            return this;
        }

        public Søknad build() {
            Objects.requireNonNull(kladd.ikkeUtlandTreMåneder, "Må ha svart på utenlandsopphold de tre neste måneder");
            return kladd;
        }
    }
}
