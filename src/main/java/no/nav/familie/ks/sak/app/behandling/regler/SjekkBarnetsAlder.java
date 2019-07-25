package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.time.LocalDate;
import java.time.Period;

@RuleDocumentation(SjekkBarnetsAlder.ID)
public class SjekkBarnetsAlder extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";

    private static final int MIN_ALDER_I_MANEDER = 10;
    private static final int MAKS_ALDER_I_MANEDER = 28;

    public SjekkBarnetsAlder() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        LocalDate barnetsFødselsdato = grunnlag.getTpsFakta().getBarn().getFødselsdato();
        Period diff = Period.between(barnetsFødselsdato, LocalDate.now());
        Integer alderIManeder = diff.getYears() * 12 + diff.getMonths();
        if ((alderIManeder >= MIN_ALDER_I_MANEDER) &&
            (alderIManeder <= MAKS_ALDER_I_MANEDER) &&
                !(alderIManeder.equals(MAKS_ALDER_I_MANEDER) && diff.getDays() > 0)) {
            return nei();
        }
        return ja();
    }
}
