package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OppslagException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");

    public OppslagException(String msg) {
        super(msg);
    }

    public OppslagException(String msg, Exception e, URI uri) {
        super(msg);

        secureLogger.info("Ukjent feil ved oppslag mot {}. {}", uri, e.getMessage());
        logger.warn("Ukjent feil ved oppslag mot '" + uri + "'.");
    }
}
