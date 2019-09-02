package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.domene.typer.Tid;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.fpsak.tidsserie.LocalDateSegment;
import no.nav.fpsak.tidsserie.LocalDateTimeline;
import no.nav.fpsak.tidsserie.StandardCombinators;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RuleDocumentation(HattNorskStatsborgerskapFemÅr.ID)
public class HattNorskStatsborgerskapFemÅr extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "MEDL-2";
    private static int MIN_ANTALL_ÅR = 5;
    private static int ANTALL_DAGER_I_ÅRET = 365;

    public HattNorskStatsborgerskapFemÅr() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        PersonMedHistorikk forelder = grunnlag.getTpsFakta().getForelder();
        PersonMedHistorikk annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (norskStatsborgerskapFemÅr(forelder) && norskStatsborgerskapFemÅr(annenForelder)) {
            return ja();
        }
        return nei(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE_FEM_ÅR);
    }

    private boolean norskStatsborgerskapFemÅr(PersonMedHistorikk forelder) {
        if (forelder == null) {
            return false;
        }
        var segmenter = forelder.getPersonhistorikkInfo().getStatsborgerskaphistorikk().stream()
                .filter( periode -> periode.getTilhørendeLand().erNorge() )
                .map(StatsborgerskapPeriode::getPeriode)
                .map(periode -> new LocalDateSegment<>(periode.getFom(), periode.getTom() != Tid.TIDENES_ENDE ? periode.getTom() : LocalDate.now(), true))
                .collect(Collectors.toList());

        final var statsborgerskapTidslinje = new LocalDateTimeline<>(segmenter, StandardCombinators::alwaysTrueForMatch).compress();
        final var antallDagerINorge = statsborgerskapTidslinje.getDatoIntervaller().stream().map(LocalDateInterval::totalDays).reduce(0L, Long::sum);

        return antallDagerINorge >= MIN_ANTALL_ÅR * ANTALL_DAGER_I_ÅRET;
    }
}
