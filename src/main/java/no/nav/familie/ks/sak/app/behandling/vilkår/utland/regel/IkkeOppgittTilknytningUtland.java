package no.nav.familie.ks.sak.app.behandling.vilkår.utland.regel;

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
        boolean arbeidIUtlandet = søknad.arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkel.equalsIgnoreCase("NEI");
        boolean utenlandskeYtelser = søknad.utenlandskeYtelser.mottarYtelserFraUtland.equalsIgnoreCase("NEI");
        boolean utenlandskKontantstotte = søknad.utenlandskKontantstotte.mottarKontantstotteFraUtlandet.equalsIgnoreCase("NEI");
        boolean annenForelderIkkeTilknytningUtland = søknad.familieforhold.annenForelderNavn.isEmpty() || annenForelderTilknytningUtland(søknad);

        return (boddEllerJobbetINorgeMinstFemAar && arbeidIUtlandet && utenlandskeYtelser && utenlandskKontantstotte && annenForelderIkkeTilknytningUtland)
                ? ja()
                : nei();
    }

    private boolean annenForelderTilknytningUtland(Søknad søknad) {
        boolean boddEllerJobbetINorgeMinstFemAar = søknad.tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAar.equals(TilknytningTilUtland.TilknytningTilUtlandVerdier.jaINorge);
        boolean arbeidIUtlandet = søknad.arbeidIUtlandet.arbeiderAnnenForelderIUtlandet.equalsIgnoreCase("NEI");
        boolean utenlandskeYtelser = søknad.utenlandskeYtelser.mottarAnnenForelderYtelserFraUtland.equalsIgnoreCase("NEI");
        return boddEllerJobbetINorgeMinstFemAar && arbeidIUtlandet && utenlandskeYtelser;
    }
}
