package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.domene.typer.Tid;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.fpsak.tidsserie.LocalDateSegment;
import no.nav.fpsak.tidsserie.LocalDateTimeline;
import no.nav.fpsak.tidsserie.StandardCombinators;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RuleDocumentation(HarVærtBosattFemÅrINorge.ID)
public class HarVærtBosattFemÅrINorge extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-MEDL-1";
    private static int MIN_ANTALL_ÅR = 5;
    private static int ANTALL_DAGER_I_ÅRET = 365;

    public HarVærtBosattFemÅrINorge() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Forelder forelder = grunnlag.getTpsFakta().getForelder();
        Forelder annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (bosattFemÅrINorge(forelder) || bosattFemÅrINorge(annenForelder)) {
            return ja();
        }
        return nei(VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_FEM_ÅR);
    }

    private boolean bosattFemÅrINorge(Forelder forelder) {
        if (forelder == null) {
            return false;
        }
        var segmenter = forelder.getPersonhistorikkInfo().getAdressehistorikk().stream()
                .filter(this::erNorskBostedsadresse)
                .map(AdressePeriode::getPeriode)
                .map(periode -> new LocalDateSegment<>(periode.getFom(), periode.getTom() != Tid.TIDENES_ENDE ? periode.getTom() : LocalDate.now(), true))
                .collect(Collectors.toList());

        final var bostedstidslinje = new LocalDateTimeline<>(segmenter, StandardCombinators::alwaysTrueForMatch).compress();
        final var antallDagerINorge = bostedstidslinje.getDatoIntervaller().stream().map(LocalDateInterval::totalDays).reduce(0L, Long::sum);

        return antallDagerINorge >= MIN_ANTALL_ÅR * ANTALL_DAGER_I_ÅRET;
    }

    private boolean erNorskBostedsadresse(AdressePeriode adressePeriode) {
        return adressePeriode.getAdresse().getLand().equals(Landkode.NORGE.getKode()) && adressePeriode.getAdresse().getAdresseType().equals(AdresseType.BOSTEDSADRESSE);
    }
}
