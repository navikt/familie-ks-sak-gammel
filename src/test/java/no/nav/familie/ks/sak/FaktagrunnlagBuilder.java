package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.*;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;

class FaktagrunnlagBuilder {

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";

    private static ObjectMapper mapper =  new ObjectMapper();

    FaktagrunnlagBuilder() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    Faktagrunnlag ugyldig() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaUgyldig)
                .medSøknad(medBarnehageplass())
                .build();
    }

    Faktagrunnlag gyldig() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaGyldig)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    private Søknad utenBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadUtenBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private Søknad medBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private Personinfo personinfoNorsk = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.NORGE)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private Personinfo personinfoUtland = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.UDEFINERT)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("annen adresse")
            .medNavn("test testesen")
            .build();

    private Personinfo barnGyldigAlder = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private Personinfo barnUgyldigAlder = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(5))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder()
            .medLand(STATSBORGERSKAP_GYLDIG)
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medGyldighetsperiode(
                    new Periode(LocalDate.now().minusYears(6), LocalDate.now())
            )
            .build();

    private AdressePeriode norskAdresseEtÅr = new AdressePeriode.Builder()
            .medLand(STATSBORGERSKAP_GYLDIG)
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medGyldighetsperiode(
                    new Periode(LocalDate.now().minusYears(1), LocalDate.now())
            )
            .build();

    private PersonhistorikkInfo boddINorgeSeksÅr = new PersonhistorikkInfo.Builder()
            .medPersonIdent("12345678910")
            .leggTil(norskAdresseSeksÅr)
            .build();

    private PersonhistorikkInfo boddINorgeEtÅr = new PersonhistorikkInfo.Builder()
            .medPersonIdent("12345678910")
            .leggTil(norskAdresseEtÅr)
            .build();

    private Forelder forelderNorsk = new Forelder.Builder()
            .medPersoninfo(personinfoNorsk)
            .medPersonhistorikkInfo(boddINorgeSeksÅr)
            .build();

    private Forelder forelderUtland = new Forelder.Builder()
            .medPersoninfo(personinfoUtland)
            .medPersonhistorikkInfo(boddINorgeEtÅr)
            .build();

    TpsFakta tpsFaktaGyldig = new TpsFakta.Builder()
            .medForelder(forelderNorsk)
            .medAnnenForelder(forelderNorsk)
            .medBarn(barnGyldigAlder)
            .build();

    TpsFakta tpsFaktaUgyldig = new TpsFakta.Builder()
            .medForelder(forelderUtland)
            .medAnnenForelder(forelderNorsk)
            .medBarn(barnUgyldigAlder)
            .build();

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
