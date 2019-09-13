package no.nav.familie.ks.sak.app.behandling.regel.mvp.utland.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(IkkeOppgittTilknytningUtland.ID)
public class IkkeOppgittTilknytningUtland extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-UTL-1";

    public IkkeOppgittTilknytningUtland() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Søknad søknad = grunnlag.getSøknad();
        boolean boddEllerJobbetINorgeMinstFemAar = søknad.tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar.equals(TilknytningTilUtland.TilknytningTilUtlandVerdier.jaINorge);
        boolean arbeidIUtlandet = søknad.arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkel.equals(Standpunkt.NEI.getKode());
        boolean utenlandskeYtelser = søknad.utenlandskeYtelser.mottarYtelserFraUtland.equals(Standpunkt.NEI.getKode());
        boolean utenlandskKontantstotte = søknad.utenlandskKontantstotte.mottarKontantstotteFraUtlandet.equals(Standpunkt.NEI.getKode());
        boolean annenForelderIkkeTilknytningUtland = søknad.getFamilieforhold().getAnnenForelderFødselsnummer().isEmpty() || annenForelderIkkeTilknytningUtland(søknad);

        return (boddEllerJobbetINorgeMinstFemAar && arbeidIUtlandet && utenlandskeYtelser && utenlandskKontantstotte && annenForelderIkkeTilknytningUtland)
                ? ja()
                : nei();
    }

    private boolean annenForelderIkkeTilknytningUtland(Søknad søknad) {
        boolean boddEllerJobbetINorgeMinstFemAar = søknad.tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAar.equals(TilknytningTilUtland.TilknytningTilUtlandVerdier.jaINorge);
        boolean arbeidIUtlandet = søknad.arbeidIUtlandet.arbeiderAnnenForelderIUtlandet.equals(Standpunkt.NEI.getKode());
        boolean utenlandskeYtelser = søknad.utenlandskeYtelser.mottarAnnenForelderYtelserFraUtland.equals(Standpunkt.NEI.getKode());
        return boddEllerJobbetINorgeMinstFemAar && arbeidIUtlandet && utenlandskeYtelser;
    }
}
