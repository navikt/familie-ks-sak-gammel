package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.time.LocalDate;
import java.time.Period;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";
    private static final String GYLDIG_STATSBORGERSKAP = "NOR";
    private static int MIN_ANTALL_ÅR = 5;
    private static int ANTALL_MÅNEDER_I_ÅRET = 12;

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Forelder forelder = grunnlag.getTpsFakta().getForelder();
        Forelder annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (! norskMedlemsskap(forelder.getPersonhistorikkInfo())
                || (annenForelder != null && ! norskMedlemsskap(annenForelder.getPersonhistorikkInfo()))) {
            return ja();
        }
        return nei();
    }

    private boolean norskMedlemsskap(PersonhistorikkInfo personhistorikkInfo) {
        Landkode statsborgerskap = personhistorikkInfo.getStatsborgerskaphistorikk().stream()
                .filter( periode -> periode.getPeriode().getTom().equals(Tid.TIDENES_ENDE))
                .findFirst()
                .get()
                .getTilhørendeLand();
        int antallMånederINorge = personhistorikkInfo.getAdressehistorikk().stream()
                .filter(this::erNorskBostedsadresse)
                .map(AdressePeriode::getPeriode)
                .map( periode -> periode.getTom().equals(Tid.TIDENES_ENDE)
                        ? Period.between(periode.getFom(), LocalDate.now())
                        : Period.between(periode.getFom(), periode.getTom()))
                .map( periode -> periode.getYears() * ANTALL_MÅNEDER_I_ÅRET + periode.getMonths())
                .mapToInt(Integer::intValue)
                .sum();

        boolean boddINorgeFemÅr = antallMånederINorge >= MIN_ANTALL_ÅR * ANTALL_MÅNEDER_I_ÅRET;
        return statsborgerskap.erNorge() && boddINorgeFemÅr;
    }

    private boolean erNorskBostedsadresse(AdressePeriode adressePeriode) {
        return adressePeriode.getAdresse().getLand().equals(GYLDIG_STATSBORGERSKAP) && adressePeriode.getAdresse().getAdresseType().equals(AdresseType.BOSTEDSADRESSE);
    }
}
