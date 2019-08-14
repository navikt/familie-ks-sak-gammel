package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.util.Optional;

@RuleDocumentation(MinstEnErNorskStatsborger.ID)
public class MinstEnErNorskStatsborger extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "MEDL-2";

    public MinstEnErNorskStatsborger() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Forelder forelder = grunnlag.getTpsFakta().getForelder();
        Forelder annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (norskStatsborger(forelder) || norskStatsborger(annenForelder)) {
            return ja();
        }
        return nei(VilkårIkkeOppfyltÅrsak.IKKE_NORSK_STATSBORGER);
    }

    private boolean norskStatsborger(Forelder forelder) {
        if (forelder == null) {
            return false;
        }
        Optional<Landkode> statsborgerskap = forelder.getPersonhistorikkInfo().getStatsborgerskaphistorikk().stream()
                .filter(periode -> periode.getPeriode().getTom().equals(Tid.TIDENES_ENDE))
                .filter(periode -> periode.getTilhørendeLand().erNorge())
                .map(StatsborgerskapPeriode::getTilhørendeLand)
                .findFirst();

        return statsborgerskap.isPresent();
    }
}
