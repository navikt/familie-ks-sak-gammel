package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OppslagExceptionTest {


    @Test
    public void skal_returnere_blank_resonseBody_hvis_ikke_RestCLientResponseException() {
        OppslagException exception = new OppslagException("test", new RuntimeException(), null, null);
        assertNull(exception.getResponseBodyAsString());
    }

    @Test
    public void skal_returnere_resonseBody_hvis_RestCLientResponseException() {
        OppslagException exception = new OppslagException("test", new RestClientResponseException("test", 400, "test", null, "respons æøå".getBytes(), Charset.defaultCharset()), null, null);
        assertEquals("respons æøå", exception.getResponseBodyAsString());
    }

}
