package no.nav.familie.ks.sak;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.behandling.Regelresultat;
import no.nav.familie.ks.sak.app.behandling.VilkårRegel;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regler.*;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.*;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class RegelTest {

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final LocalDate FØDSELSDATO_BARN_UGYLDIG = LocalDate.now().minusMonths(9);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final String STATSBORGERSKAP_UGYLDIG = "NZ";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_UGYLDIG = false;

    private static ObjectMapper mapper =  new ObjectMapper();

    @Before
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private final VilkårRegel vilkårRegel = new VilkårRegel();


    @Test
    public void at_sjekkBarnetsAlder_returnerer_korrekt() {
        SjekkBarnetsAlder sjekkBarnetsAlder = new SjekkBarnetsAlder();

        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = sjekkBarnetsAlder.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigTpsFakta();
        evaluation = sjekkBarnetsAlder.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkBarnSammeAdresse_returnerer_korrekt() {
        SjekkBarnSammeAdresse sjekkBarnSammeAdresse = new SjekkBarnSammeAdresse();

        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = sjekkBarnSammeAdresse.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigTpsFakta();
        evaluation = sjekkBarnSammeAdresse.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkDeltBosted_returnerer_korrekt() {
        SjekkDeltBosted sjekkDeltBosted = new SjekkDeltBosted();

        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = sjekkDeltBosted.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknad();
        evaluation = sjekkDeltBosted.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkMedlemsskap_returnerer_korrekt() {
        SjekkMedlemsskap sjekkMedlemsskap = new SjekkMedlemsskap();

        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigTpsFakta();
        evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkTreÅrUtland_returnerer_korrekt() {
        SjekkTreÅrUtland sjekkTreÅrUtland = new SjekkTreÅrUtland();

        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = sjekkTreÅrUtland.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknad();
        evaluation = sjekkTreÅrUtland.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_vilkår_regel_blir_oppfylt() {
        Faktagrunnlag faktagrunnlag = medGyldigTpsFaktaOgSøknad();
        Evaluation evaluation = vilkårRegel.evaluer(faktagrunnlag);
        Regelresultat regelresultat = new Regelresultat(evaluation);
        assertThat(regelresultat.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    private Søknad søknad() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehage.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private Søknad søknadUgyldigeVilkår() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehageUgyldig.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private TpsFakta tpsFaktaGyldig() {
        return new TpsFakta.Builder()
                .medForelder(forelderOk)
                .medAnnenForelder(forelderOk)
                .medBarn(barnKsAlderPersoninfo)
                .build();
    }

    private TpsFakta tpsFaktaUgyldig() {
        return new TpsFakta.Builder()
                .medForelder(forelderIkkeOk)
                .medAnnenForelder(forelderOk)
                .medBarn(barnIkkeKsAlderPersoninfo)
                .build();
    }

    private Faktagrunnlag medUgyldigTpsFakta() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaUgyldig())
                .medSøknad(søknad())
                .build();
    }

    private Faktagrunnlag medGyldigTpsFaktaOgSøknad() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaGyldig())
                .medSøknad(søknad())
                .build();
    }

    private Faktagrunnlag medUgyldigSøknad() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaGyldig())
                .medSøknad(søknadUgyldigeVilkår())
                .build();
    }


    private Periode minstFemÅr = new Periode(LocalDate.now().minusYears(6), LocalDate.now());
    private Periode mindreEnnFemÅr = new Periode(LocalDate.now().minusYears(1), LocalDate.now());
    private AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder().medLand("NOR").medGyldighetsperiode(minstFemÅr).build();
    private AdressePeriode norskAdresseEtÅr = new AdressePeriode.Builder().medLand("NOR").medGyldighetsperiode(mindreEnnFemÅr).build();


    private Personinfo norskPersoninfo = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.NORGE)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private Personinfo utlandPersoninfo = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.UDEFINERT)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("annen adresse")
            .medNavn("test testesen")
            .build();

    private Personinfo barnKsAlderPersoninfo = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();


    private Personinfo barnIkkeKsAlderPersoninfo = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(5))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private PersonhistorikkInfo femÅrPersonInfoHistorikk = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseEtÅr)
            .build();

    private PersonhistorikkInfo mindreEnnFemÅrPersonInfoHistorikk = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseEtÅr)
            .build();

    private Forelder forelderOk = new Forelder.Builder()
            .medPersoninfo(norskPersoninfo)
            .medPersonhistorikkInfo(femÅrPersonInfoHistorikk)
            .build();

    private Forelder forelderIkkeOk = new Forelder.Builder()
            .medPersoninfo(utlandPersoninfo)
            .medPersonhistorikkInfo(mindreEnnFemÅrPersonInfoHistorikk)
            .build();

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
