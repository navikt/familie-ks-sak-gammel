package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.*;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;

final class FaktagrunnlagBuilder {

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    static Faktagrunnlag beggeForeldreUtenlandskeStatsborgereOgBarnForGammel() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreUtenlandskeStatsborgereOgBarnForGammel)
                .medSøknad(medBarnehageplass())
                .build();
    }

    static Faktagrunnlag beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    private static Søknad utenBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadUtenBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static Søknad medBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static Personinfo personinfoNorsk = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.NORGE)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private static Personinfo personinfoUtland = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.UDEFINERT)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("annen adresse")
            .medNavn("test testesen")
            .build();

    private static Personinfo barnGyldigAlder = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private static Personinfo barnUgyldigAlder = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(5))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private static AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder()
            .medLand(STATSBORGERSKAP_GYLDIG)
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medGyldighetsperiode(
                    new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE)
            )
            .build();

    private static AdressePeriode norskAdresseEtÅr = new AdressePeriode.Builder()
            .medLand(STATSBORGERSKAP_GYLDIG)
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medGyldighetsperiode(
                    new Periode(LocalDate.now().minusYears(1), Tid.TIDENES_ENDE)
            )
            .build();

    private static StatsborgerskapPeriode norskStatsborgerskapSeksÅr = new StatsborgerskapPeriode(
            new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE),
            Landkode.NORGE);

    private static StatsborgerskapPeriode norskStatsborgerskapEtÅr = new StatsborgerskapPeriode(
            new Periode(LocalDate.now().minusYears(1), Tid.TIDENES_ENDE),
            Landkode.NORGE);

    private static PersonhistorikkInfo norgeSeksÅr = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseSeksÅr)
            .leggTil(norskStatsborgerskapSeksÅr)
            .build();

    private static PersonhistorikkInfo norgeEtÅr = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseEtÅr)
            .leggTil(norskStatsborgerskapEtÅr)
            .build();

    private static Forelder forelderNorsk = new Forelder.Builder()
            .medPersoninfo(personinfoNorsk)
            .medPersonhistorikkInfo(norgeSeksÅr)
            .build();

    private static Forelder forelderUtland = new Forelder.Builder()
            .medPersoninfo(personinfoUtland)
            .medPersonhistorikkInfo(norgeEtÅr)
            .build();

    static TpsFakta beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder = new TpsFakta.Builder()
            .medForelder(forelderNorsk)
            .medAnnenForelder(forelderNorsk)
            .medBarn(barnGyldigAlder)
            .build();

    static TpsFakta beggeForeldreUtenlandskeStatsborgereOgBarnForGammel = new TpsFakta.Builder()
            .medForelder(forelderUtland)
            .medAnnenForelder(forelderUtland)
            .medBarn(barnUgyldigAlder)
            .build();

    private static String getFile(String filnavn) {
        return FaktagrunnlagBuilder.class.getClassLoader().getResource(filnavn).getFile();
    }
}
