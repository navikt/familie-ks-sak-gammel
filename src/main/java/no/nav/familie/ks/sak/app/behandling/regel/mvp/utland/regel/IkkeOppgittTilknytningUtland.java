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
        PersonMedHistorikk søkerMedHistorikk = grunnlag.getTpsFakta().getForelder();
        PersonMedHistorikk annenPartMedHistorikk = grunnlag.getTpsFakta().getAnnenForelder();
        AktørId søker = søkerMedHistorikk.getPersoninfo().getAktørId();

        SøknadGrunnlag søknadGrunnlag = grunnlag.getSøknadGrunnlag();
        no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad søknad = søknadGrunnlag.getSøknad();

        boolean søkerOppfyllerKrav = ikkeTilknytningTilUtland(søker, søkerMedHistorikk, søknad);

        boolean annenPartOppfyllerKrav = true;
        if (annenPartMedHistorikk != null &&
            søknad.getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet().size() > 1 &&
            søknad.getUtlandsTilknytning().getAktørerTilknytningTilUtlandet().size() > 1) {
            annenPartOppfyllerKrav = ikkeTilknytningTilUtland(søker, annenPartMedHistorikk, søknad);
        }

        return søkerOppfyllerKrav && annenPartOppfyllerKrav ? ja() : nei();
    }

    private boolean ikkeTilknytningTilUtland(AktørId søker, PersonMedHistorikk personMedHistorikk, no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad søknad) {
        AktørId aktør = personMedHistorikk.getPersoninfo().getAktørId();

        final Set<AktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet = søknad.getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet();
        final Optional<AktørArbeidYtelseUtland> aktørArbeidYtelseUtland = aktørerArbeidYtelseIUtlandet.stream().filter(aktørArbeidYtelseIUtlandet ->
            aktørArbeidYtelseIUtlandet.getAktørId().equals(aktør)).findFirst();

        final Set<AktørTilknytningUtland> aktørerTilknytningTilUtlandet = søknad.getUtlandsTilknytning().getAktørerTilknytningTilUtlandet();
        final Optional<AktørTilknytningUtland> aktørTilknytningTilUtlandet = aktørerTilknytningTilUtlandet.stream().filter(aktørTilknytningUtland ->
            aktørTilknytningUtland.getAktør().equals(aktør)).findFirst();

        if (!aktørArbeidYtelseUtland.isPresent() || !aktørTilknytningTilUtlandet.isPresent()) {
            return false;
        }

        boolean boddEllerJobbetINorgeMinstFemAar = aktørTilknytningTilUtlandet.get().getTilknytningTilUtland().equals(TilknytningTilUtlandVerdier.jaINorge);
        boolean arbeidIUtlandet = aktørArbeidYtelseUtland.get().getArbeidIUtlandet().equals(Standpunkt.NEI);
        boolean utenlandskeYtelser = aktørArbeidYtelseUtland.get().getYtelseIUtlandet().equals(Standpunkt.NEI);

        boolean utenlandskKontantstotte = true;
        if (aktør.equals(søker)) {
            utenlandskKontantstotte = aktørArbeidYtelseUtland.get().getKontantstøtteIUtlandet().equals(Standpunkt.NEI);
        }

        return boddEllerJobbetINorgeMinstFemAar && arbeidIUtlandet && utenlandskeYtelser && utenlandskKontantstotte;
    }
}
