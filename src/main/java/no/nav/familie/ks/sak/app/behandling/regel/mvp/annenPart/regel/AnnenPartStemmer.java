package no.nav.familie.ks.sak.app.behandling.regel.mvp.annenPart.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(AnnenPartStemmer.ID)
public class AnnenPartStemmer extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BARN-1";

    public AnnenPartStemmer() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        PersonMedHistorikk annenPart = grunnlag.getTpsFakta().getAnnenForelder();
        String oppgittAnnenPartPersonIdent = grunnlag.getSøknadGrunnlag().getSøknad().getOppgittAnnenPartFødselsnummer();

        if (annenPart == null && oppgittAnnenPartPersonIdent.equals("")) {
            return ja();
        } else if (annenPart == null && !oppgittAnnenPartPersonIdent.equals("")) {
            return nei();
        } else if (annenPart != null && oppgittAnnenPartPersonIdent.equals("")) {
            return nei();
        }

        assert annenPart != null;

        if (annenPart.getPersoninfo().getPersonIdent().getIdent().regionMatches(0, oppgittAnnenPartPersonIdent, 0, 6)) {
            return ja();
        } else {
            return nei();
        }
    }
}
