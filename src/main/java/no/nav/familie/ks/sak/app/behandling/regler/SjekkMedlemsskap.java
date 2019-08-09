package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regler.medlemskap.HarVærtBosattFemÅrINorge;
import no.nav.familie.ks.sak.app.behandling.regler.medlemskap.MinstEnErNorskStatsborger;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.AndSpecification;
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

    public static final String ID = "MEDL-root";

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        return new AndSpecification<>(new MinstEnErNorskStatsborger(),
                new HarVærtBosattFemÅrINorge())
                .evaluate(grunnlag);
    }

}
