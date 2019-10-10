package no.nav.familie.ks.sak.app.behandling.fastsetting;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.InfotrygdFakta;

import java.time.LocalDate;
import java.util.Objects;

public class Faktagrunnlag {
    private final LocalDate behandlingstidspunkt;
    private BarnehageBarnGrunnlag barnehageBarnGrunnlag;
    private SøknadGrunnlag søknadGrunnlag;
    private TpsFakta tpsFakta;
    private InfotrygdFakta infotrygdFakta;

    public Faktagrunnlag() {
        behandlingstidspunkt = LocalDate.now();
    }

    public LocalDate getBehandlingstidspunkt() {
        return behandlingstidspunkt;
    }

    public BarnehageBarnGrunnlag getBarnehageBarnGrunnlag() {
        return barnehageBarnGrunnlag;
    }

    public SøknadGrunnlag getSøknadGrunnlag() {
        return søknadGrunnlag;
    }

    public TpsFakta getTpsFakta() {
        return tpsFakta;
    }

    public InfotrygdFakta getInfotrygdFakta() {
        return infotrygdFakta;
    }

    public static final class Builder {

        private Faktagrunnlag kladd;

        public Builder() {
            kladd = new Faktagrunnlag();
        }

        public Faktagrunnlag.Builder medBarnehageBarnGrunnlag(BarnehageBarnGrunnlag barnehageBarnGrunnlag) {
            kladd.barnehageBarnGrunnlag = barnehageBarnGrunnlag;
            return this;
        }

        public Faktagrunnlag.Builder medSøknadGrunnlag(SøknadGrunnlag søknadGrunnlag) {
            kladd.søknadGrunnlag = søknadGrunnlag;
            return this;
        }

        public Faktagrunnlag.Builder medTpsFakta(TpsFakta tpsFakta) {
            kladd.tpsFakta = tpsFakta;
            return this;
        }

        public Faktagrunnlag.Builder medInfotrygdFakta(InfotrygdFakta infotrygdFakta) {
            kladd.infotrygdFakta = infotrygdFakta;
            return this;
        }

        public Faktagrunnlag build() {
            Objects.requireNonNull(kladd.søknadGrunnlag, "Må ha opplysninger fra fastsetting");
            Objects.requireNonNull(kladd.tpsFakta, "Må ha opplysninger fra TPS");
            return kladd;
        }
    }
}
