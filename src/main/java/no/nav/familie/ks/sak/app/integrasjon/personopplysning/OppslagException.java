package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import kotlin.text.Charsets;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OppslagException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");

    private byte[] responseBody;

    public OppslagException(String msg) {
        super(msg);
    }

    public OppslagException(String msg, Exception e, URI uri, String ident) {
        super(msg, e);

        String message = "";
        if (e instanceof RestClientResponseException) {
            message = ((RestClientResponseException) e).getResponseBodyAsString();
            responseBody = ((RestClientResponseException) e).getResponseBodyAsByteArray();
        }

        secureLogger.info("Ukjent feil ved oppslag mot {}. ident={} {} {}", uri, ident, message, e);
        logger.warn("Ukjent feil ved oppslag mot '{}'.", uri);
    }

    public String getResponseBodyAsString() {
        if (responseBody == null) {
            return null;
        }
        return new String(this.responseBody, Charsets.UTF_8);
    }
}
