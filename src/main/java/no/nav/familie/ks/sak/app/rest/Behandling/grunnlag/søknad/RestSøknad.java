package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import java.time.LocalDateTime;

public class RestSøknad {
    public LocalDateTime innsendtTidspunkt;
    public RestOppgittFamilieforhold familieforhold;
    public RestOppgittUtlandsTilknytning utlandsTilknytning;
    public RestOppgittErklæring erklæring;

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
