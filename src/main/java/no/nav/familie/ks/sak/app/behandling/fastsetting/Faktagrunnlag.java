package no.nav.familie.ks.sak.app.behandling.fastsetting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.VilkårRegelFeil;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;

import java.time.LocalDate;
import java.util.Objects;

public class Faktagrunnlag {

    private final LocalDate behandlingstidspunkt;
    private TpsFakta tpsFakta;
    private Søknad søknad;
    private String somJson;

    public Faktagrunnlag() {
        behandlingstidspunkt = LocalDate.now();
    }

    public LocalDate getBehandlingstidspunkt() {
        return behandlingstidspunkt;
    }

    public TpsFakta getTpsFakta() {
        return tpsFakta;
    }

    public Søknad getSøknad() {
        return søknad;
    }

    public String somJson() {
        return somJson;
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

        private void lagJson() {
            try {
                final ObjectMapper objectMapper = new JacksonJsonConfig().objectMapper();
                kladd.somJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kladd);
            } catch (JsonProcessingException e) {
                throw new VilkårRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
            }
        }

        public Faktagrunnlag build() {
            Objects.requireNonNull(kladd.søknad, "Må ha opplysninger fra fastsetting");
            Objects.requireNonNull(kladd.søknad, "Må ha opplysninger fra TPS");
            lagJson();
            return kladd;
        }
    }

}
