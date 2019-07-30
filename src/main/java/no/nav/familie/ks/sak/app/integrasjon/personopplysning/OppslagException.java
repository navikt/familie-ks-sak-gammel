package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OppslagException extends RuntimeException {
    public OppslagException(String msg) {
        super(msg);
    }
}
