package no.nav.familie.ks.sak.app.integrasjon.oppgave.domene;

import no.nav.familie.ks.kontrakter.søknad.Barn;
import no.nav.familie.ks.kontrakter.søknad.Søknad;
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
        "Medlemskap i Folketrygden (Botidskrav) oppfylt for søker: JA\n" +
        "Medlemskap i Folketrygden (Botidskrav) oppfylt for den andre forelderen: JA\n" +
        "Avtalt oppholdstid: 0 timer\n" +
        "Beløp: 7500 nkr\n" +
        "\n" +
        "Etterbetaling: NEI\n" +
        "Søknad mottatt dato: %s\n" +
        "Barnets fødselsdato: %s\n" +
        "\n" +
        "Se saksbehandlingsløsningen for kontantstøtte (NB! må åpnes i chrome): %s\n";

    String MANUELL_BEHANDLING = "\nSaken må behandles manuelt\n";

    static Object[] args(Vedtak vedtak, Søknad søknad) {
        DateTimeFormatter datoFormat = DateTimeFormatter.ofPattern("DD.MM.YYYY");

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

        return new String[]{stønadFom, mottattDato, barnetsFødselsdato, "TODO: oppgi link"};
    }
}
