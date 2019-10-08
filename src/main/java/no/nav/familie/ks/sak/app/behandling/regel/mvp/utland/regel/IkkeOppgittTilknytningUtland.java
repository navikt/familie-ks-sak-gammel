package no.nav.familie.ks.sak.app.behandling.regel.mvp.utland.regel;

import no.nav.familie.ks.kontrakter.søknad.Standpunkt;
import no.nav.familie.ks.kontrakter.søknad.TilknytningTilUtlandVerdier;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørArbeidYtelseUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RuleDocumentation(IkkeOppgittTilknytningUtland.ID)
public class IkkeOppgittTilknytningUtland extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-UTL-1";

    public IkkeOppgittTilknytningUtland() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        SøknadGrunnlag søknadGrunnlag = grunnlag.getSøknadGrunnlag();
        no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad søknad = søknadGrunnlag.getSøknad();

        return ikkeTilknytningTilUtland(søknad) ? ja() : nei();
    }

    private boolean ikkeTilknytningTilUtland(no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad søknad) {
        long tilknytningTilUtlandetCount = søknad.getUtlandsTilknytning().getAktørerTilknytningTilUtlandet().stream().map(aktørTilknytningUtland -> {
            boolean boddEllerJobbetINorgeMinstFemAar = aktørTilknytningUtland.getTilknytningTilUtland().equals(TilknytningTilUtlandVerdier.jaINorge);
            return boddEllerJobbetINorgeMinstFemAar;
        }).filter(result -> !result).count();

        long arbeidYtelseIUtlandetCount = søknad.getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet().stream().map(aktørArbeidYtelseUtland -> {
            boolean arbeidIUtlandet = aktørArbeidYtelseUtland.getArbeidIUtlandet().equals(Standpunkt.NEI);
            boolean utenlandskeYtelser = aktørArbeidYtelseUtland.getYtelseIUtlandet().equals(Standpunkt.NEI);
            boolean utenlandskKontantstotte = !aktørArbeidYtelseUtland.getKontantstøtteIUtlandet().equals(Standpunkt.JA);

            return arbeidIUtlandet && utenlandskeYtelser && utenlandskKontantstotte;
        }).filter(result -> !result).count();

        return tilknytningTilUtlandetCount == 0 && arbeidYtelseIUtlandetCount == 0;
    }
}
