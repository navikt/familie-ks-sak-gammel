package no.nav.familie.ks.sak.app.rest.Behandling;

import java.time.LocalDateTime;

public class RestSøknad {
    private LocalDateTime innsendtTidspunkt;
    private RestOppgittFamilieforhold familieforhold;
    private RestOppgittUtlandsTilknytning utlandsTilknytning;
    private RestOppgittErklæring erklæring;

    public RestSøknad(LocalDateTime innsendtTidspunkt,
                      RestOppgittFamilieforhold familieforhold,
                      RestOppgittUtlandsTilknytning utlandsTilknytning,
                      RestOppgittErklæring erklæring) {
        this.innsendtTidspunkt = innsendtTidspunkt;
        this.familieforhold = familieforhold;
        this.utlandsTilknytning = utlandsTilknytning;
        this.erklæring = erklæring;
    }
}
