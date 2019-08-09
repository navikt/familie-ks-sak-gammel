package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.fpsak.tidsserie.LocalDateSegment;
import no.nav.fpsak.tidsserie.LocalDateTimeline;
import no.nav.fpsak.tidsserie.StandardCombinators;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";
    private static final String GYLDIG_STATSBORGERSKAP = "NOR";
    private static int MIN_ANTALL_ÅR = 5;
    private static int ANTALL_DAGER_I_ÅRET = 365;

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Forelder forelder = grunnlag.getTpsFakta().getForelder();
        Forelder annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (!norskMedlemsskap(forelder.getPersonhistorikkInfo())
                || (annenForelder != null && !norskMedlemsskap(annenForelder.getPersonhistorikkInfo()))) {
            return ja();
        }
        return nei();
    }

    private boolean norskMedlemsskap(PersonhistorikkInfo personhistorikkInfo) {
        Optional<Landkode> statsborgerskap = personhistorikkInfo.getStatsborgerskaphistorikk().stream()
                .filter(periode -> periode.getPeriode().getTom().equals(Tid.TIDENES_ENDE))
                .filter(periode -> periode.getTilhørendeLand().erNorge())
                .map(StatsborgerskapPeriode::getTilhørendeLand)
                .findFirst();
        final var segmenter = personhistorikkInfo.getAdressehistorikk().stream()
                .filter(this::erNorskBostedsadresse)
                .map(AdressePeriode::getPeriode)
                .map(periode -> new LocalDateSegment<>(periode.getFom(), periode.getTom() != Tid.TIDENES_ENDE ? periode.getTom() : LocalDate.now(), true))
                .collect(Collectors.toList());

        final var bostedstidslinje = new LocalDateTimeline<>(segmenter, StandardCombinators::alwaysTrueForMatch).compress();
        final var antallDagerINorge = bostedstidslinje.getDatoIntervaller().stream().map(LocalDateInterval::totalDays).reduce(0L, Long::sum);

        boolean boddINorgeFemÅr = antallDagerINorge >= MIN_ANTALL_ÅR * ANTALL_DAGER_I_ÅRET;
        return statsborgerskap.isPresent() && boddINorgeFemÅr;
    }

    private boolean erNorskBostedsadresse(AdressePeriode adressePeriode) {
        return adressePeriode.getAdresse().getLand().equals(GYLDIG_STATSBORGERSKAP) && adressePeriode.getAdresse().getAdresseType().equals(AdresseType.BOSTEDSADRESSE);
    }
}
