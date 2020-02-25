package no.nav.familie.ks.sak.app.integrasjon.oppgave.domene;

import no.nav.familie.kontrakter.ks.søknad.Barn;
import no.nav.familie.kontrakter.ks.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public interface OppgaveBeskrivelse {
    String FORESLÅ_VEDTAK = "Forslag til vedtak fra saksbehandlingsløsningen for kontantstøtte:\n" +
        "\n" +
        "Foreslått vedtak: Innvilget\n" +
        "Bruker skal ha kontantstøtte f.o.m: %s\n" +
        "Medlemskap i Folketrygden oppfylt for søker: JA\n" +
        "Medlemskap i Folketrygden oppfylt for den andre forelderen: JA\n" +
        "Avtalt oppholdstid i barnehage: 0 timer\n" +
        "Beløp: 7500 kroner\n" +
        "\n" +
        "Søknad mottatt dato: %s\n" +
        "Barnets fødselsdato: %s\n" +
        "\n" +
        "Se saksbehandlingsløsningen for kontantstøtte (NB! Bør åpnes i Chrome): \n\n" +
        "%s\n";

    String MANUELL_BEHANDLING = "\nSaken må behandles manuelt\n";

    static Object[] args(Vedtak vedtak, Søknad søknad) {
        DateTimeFormatter datoFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String stønadFom = vedtak.getStønadperiode().getFom().format(datoFormat);
        String mottattDato = Optional.ofNullable(søknad.getInnsendtTidspunkt())
            .map(LocalDateTime::toLocalDate)
            .map(localDate -> localDate.format(datoFormat))
            .orElse("xx.xx.xxxx");
        String barnetsFødselsdato = søknad.getOppgittFamilieforhold().getBarna().stream()
            .findFirst() //TODO: skriv om når vi støtter flerlinger
            .map(Barn::getFødselsnummer)
            .map(fnr -> LocalDate.parse(fnr.substring(0, 6), DateTimeFormatter.ofPattern("ddMMyy")).format(datoFormat))
            .orElse("xx.xx.xxxx");

        String saksbehandlingUrl = "https://kontantstotte.nais.adeo.no/fagsak/";
        return new String[]{stønadFom, mottattDato, barnetsFødselsdato, saksbehandlingUrl + vedtak.getFagsakId()};
    }
}
