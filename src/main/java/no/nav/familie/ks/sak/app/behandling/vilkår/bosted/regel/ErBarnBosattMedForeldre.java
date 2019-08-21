package no.nav.familie.ks.sak.app.behandling.vilkår.bosted.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(ErBarnBosattMedForeldre.ID)
public class ErBarnBosattMedForeldre extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BOSTED-1";

    public ErBarnBosattMedForeldre() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        TpsFakta tpsFakta = grunnlag.getTpsFakta();
        if (tpsFakta.getAnnenForelder() != null &&
                borSammen(tpsFakta.getBarn(), aktørIdFor(tpsFakta.getForelder())) &&
                borSammen(tpsFakta.getBarn(), aktørIdFor(tpsFakta.getAnnenForelder()))) {
            return ja();
        } else {
            return nei();
        }
    }

    private static AktørId aktørIdFor(Forelder forelder) {
        return forelder.getPersoninfo().getAktørId();
    }

    private static boolean borSammen(Personinfo person, AktørId aktørId) {
        return person
                .getFamilierelasjoner()
                .stream()
                .filter( relasjon -> relasjon.getAktørId().equals(aktørId))
                .map(Familierelasjon::getHarSammeBosted)
                .findFirst()
                .orElse(false);
    }
}
