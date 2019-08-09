package no.nav.familie.ks.sak.app.behandling.regler.medlemskap;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.util.Optional;

@RuleDocumentation(HarVærtBosattFemÅrINorge.ID)
public class HarVærtBosattFemÅrINorge extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "MEDL-2";

    public HarVærtBosattFemÅrINorge() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Forelder forelder = grunnlag.getTpsFakta().getForelder();
        Forelder annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (bosatt(forelder) || bosatt(annenForelder)) {
            return ja();
        }
        return nei();
    }

    private boolean bosatt(Forelder forelder) {
        Optional<Landkode> statsborgerskap = forelder.getPersonhistorikkInfo().getStatsborgerskaphistorikk().stream()
                .filter(periode -> periode.getPeriode().getTom().equals(Tid.TIDENES_ENDE))
                .filter(periode -> periode.getTilhørendeLand().erNorge())
                .map(StatsborgerskapPeriode::getTilhørendeLand)
                .findFirst();

        return statsborgerskap.isPresent();
    }
}
