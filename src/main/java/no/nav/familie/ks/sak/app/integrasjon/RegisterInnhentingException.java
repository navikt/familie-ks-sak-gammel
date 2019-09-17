package no.nav.familie.ks.sak.app.integrasjon;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class RegisterInnhentingException extends Exception {
    public RegisterInnhentingException(String msg) {
        super(msg);
    }
}
