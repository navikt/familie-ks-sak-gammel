package no.nav.familie.ks.sak.app.mottak;

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.kontrakter.søknad.SøknadKt;
import no.nav.familie.ks.sak.app.behandling.avvik.AvviksVurdering;
import no.nav.familie.ks.sak.app.behandling.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.SamletVilkårsVurdering;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.oppgave.domene.OppgaveBeskrivelse;
import no.nav.familie.ks.sak.app.rest.Ressurs;
import no.nav.familie.ks.sak.config.toggle.UnleashProvider;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/mottak")
@ProtectedWithClaims(issuer = "intern")
public class MottaSøknadController {

    private static final Logger log = LoggerFactory.getLogger(MottaSøknadController.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");

    private final Counter feiledeBehandlinger = Metrics.counter("soknad.kontantstotte.funksjonell.feiledebehandlinger");

    private final FunksjonelleMetrikker funksjonelleMetrikker;
    private final Saksbehandling saksbehandling;

    @Autowired
    public MottaSøknadController(Saksbehandling saksbehandling, FunksjonelleMetrikker funksjonelleMetrikker) {
        this.funksjonelleMetrikker = funksjonelleMetrikker;
        this.saksbehandling = saksbehandling;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Ressurs handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Valideringsfeil ved mottaDokument.");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            sb.append(" ");
            sb.append(fieldName);
            sb.append("=");
            sb.append(errorMessage);
        });
        return Ressurs.Companion.failure(sb.toString(), ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingKotlinParameterException.class)
    public Ressurs handleMissingKotlinParameterException(MissingKotlinParameterException kotEx) {
        log.error("MissingKotlinParameterException ved validering av søknadJson");
        secureLogger.error("Feil ved validering av søknadJson. message{}", kotEx.getMsg()); //message innholder fnr
        return Ressurs.Companion.failure("MissingKotlinParameterException ved validering av søknadJson", null);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    public ResponseEntity<Ressurs> mottaDokument(@Valid @RequestBody SøknadDto søknadDto) {
        Søknad søknad = SøknadKt.toSøknad(søknadDto.getSøknadJson());
        String saksnummer = søknadDto.getSaksnummer();
        String journalpostID = søknadDto.getJournalpostID();

        try {
            Vedtak vedtak = saksbehandling.behandle(søknad, saksnummer, journalpostID);
            funksjonelleMetrikker.tellFunksjonelleMetrikker(søknad, vedtak);
            return new ResponseEntity<>(Ressurs.Companion.success("Motta dokument vellykket"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("behandling feilet", e);
            feiledeBehandlinger.increment();
            Ressurs failure = Ressurs.Companion.failure("mottaDokument feilet", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failure);
        }
    }
}
