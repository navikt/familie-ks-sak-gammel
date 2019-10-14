package no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.regel;

import no.nav.familie.ks.sak.app.behandling.domene.typer.IdentType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

@RuleDocumentation(ErBarnBosattMedForeldre.ID)
public class ErBarnBosattMedForeldre extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BOSTED-1";
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");

    public ErBarnBosattMedForeldre() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        TpsFakta tpsFakta = grunnlag.getTpsFakta();
        if (tpsFakta.getAnnenForelder() != null &&
                borSammen(tpsFakta.getBarn().getPersoninfo(), personIdentFor(tpsFakta.getForelder())) &&
                borSammen(tpsFakta.getBarn().getPersoninfo(), personIdentFor(tpsFakta.getAnnenForelder()))) {
            return ja();
        } else {
            return nei();
        }
    }

    private static PersonIdent personIdentFor(PersonMedHistorikk forelder) {
        return forelder.getPersoninfo().getPersonIdent();
    }

    private static PersonIdent personIdentFor(Familierelasjon familierelasjon) {
        if (familierelasjon.getIdent().get(IdentType.PERSONIDENT) != null) {
            return (PersonIdent) familierelasjon.getIdent().get(IdentType.PERSONIDENT);
        } else {
            secureLogger.warn("Fant ikke personident for familierelasjon {}", familierelasjon);
            throw new NoSuchElementException("Fant ikke Personident for familierelasjon");
        }
    }

    private static boolean borSammen(Personinfo person, PersonIdent personIdent) {
        return person
                .getFamilierelasjoner()
                .stream()
                .filter( relasjon -> personIdentFor(relasjon).equals(personIdent))
                .map(Familierelasjon::getHarSammeBosted)
                .findFirst()
                .orElse(false);
    }
}
