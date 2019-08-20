package no.nav.familie.ks.sak.app.behandling.vilkår.bosted.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(BosattSammen.ID)
public class BosattSammen extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BOSTED-1";

    public BosattSammen() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        TpsFakta tpsFakta = grunnlag.getTpsFakta();

        if (borSammen(tpsFakta.getBarn(), identFor(tpsFakta.getForelder()))) {
            if (tpsFakta.getAnnenForelder() != null) {
                return borSammen(tpsFakta.getBarn(), identFor(tpsFakta.getAnnenForelder())) ? ja() : nei();
            }
            return ja();
        } else {
            return nei();
        }
    }

    private static String identFor(Forelder forelder) {
        return forelder.getPersoninfo().getPersonIdent().getIdent();
    }

    private static boolean borSammen(Personinfo person, String ident) {
        return person
                .getFamilierelasjoner()
                .stream()
                .filter( relasjon -> relasjon.getPersonIdent().getIdent().equals(ident))
                .map(Familierelasjon::getHarSammeBosted)
                .findFirst()
                .orElse(false);
    }
}
