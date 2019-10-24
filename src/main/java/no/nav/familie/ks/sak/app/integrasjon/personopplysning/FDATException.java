package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDATException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterInnhentingService.class);
    private static final String MSG = "Søker eller medforelder er registrert med FDAT-ident på relasjon til barnet i TPS. Klarer ikke hente registeropplysninger";

    public FDATException() {
        super(MSG);

        LOGGER.info(MSG);
    }
}
