package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.Tid;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.*;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public final class FaktagrunnlagBuilder {

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static ObjectMapper mapper = new JacksonJsonConfig().objectMapper();
    private static Personinfo personinfoNorsk = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.NORGE)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("00000000001"))
            .medPersonIdent(new PersonIdent("00000000001"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();
    private static Personinfo personinfoSvensk = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.SVERIGE)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("00000000002"))
            .medPersonIdent(new PersonIdent("00000000002"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();
    private static Personinfo personinfoUtland = new Personinfo.Builder()
            .medStatsborgerskap(Landkode.UDEFINERT)
            .medFødselsdato(LocalDate.now().minusYears(30))
            .medAktørId(new AktørId("00000000002"))
            .medPersonIdent(new PersonIdent("00000000002"))
            .medAdresse("annen adresse")
            .medNavn("test testesen")
            .build();
    private static Familierelasjon norskForelderRelasjon = new Familierelasjon(
            personinfoNorsk.getAktørId(),
            RelasjonsRolleType.FARA,
            null,
            true);
    private static Familierelasjon utenlandskForelderRelasjonMedAnnetBosted = new Familierelasjon(
            personinfoUtland.getAktørId(),
            RelasjonsRolleType.MORA,
            null,
            false);
    private static Personinfo barnNorsk = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medStatsborgerskap(Landkode.NORGE)
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .medFamilierelasjon(new HashSet<>(Collections.singleton(norskForelderRelasjon)))
            .build();
    private static Personinfo barnUtland = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(5))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medStatsborgerskap(Landkode.SVERIGE)
            .medAdresse("svensk adresse")
            .medNavn("test testesen")
            .build();
    private static Personinfo barnNorskMedNorskOgUtenlandskForelder = new Personinfo.Builder()
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medStatsborgerskap(Landkode.NORGE)
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .medFamilierelasjon(new HashSet<>(Arrays.asList(norskForelderRelasjon, utenlandskForelderRelasjonMedAnnetBosted)))
            .build();
    private static AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder()
            .medLand(STATSBORGERSKAP_GYLDIG)
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medGyldighetsperiode(
                    new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE)
            )
            .build();
    private static AdressePeriode utenlandskAdresseSeksÅr = new AdressePeriode.Builder()
            .medLand(Landkode.UDEFINERT.getKode())
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
    private static StatsborgerskapPeriode svenskStatsborger = new StatsborgerskapPeriode(
            new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE),
            Landkode.SVERIGE);
    private static StatsborgerskapPeriode norskStatsborgerskapEtÅr = new StatsborgerskapPeriode(
            new Periode(LocalDate.now().minusYears(1), Tid.TIDENES_ENDE),
            Landkode.NORGE);
    private static PersonhistorikkInfo norgeSeksÅr = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseSeksÅr)
            .leggTil(norskStatsborgerskapSeksÅr)
            .build();
    private static PersonhistorikkInfo svenskMenNorgeSeksÅr = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(norskAdresseSeksÅr)
            .leggTil(svenskStatsborger)
            .build();
    private static PersonhistorikkInfo norskMenUtlandEtÅr = new PersonhistorikkInfo.Builder()
            .medAktørId("12345678910")
            .leggTil(utenlandskAdresseSeksÅr)
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
    private static Forelder forelderNorskMenBoddIUtland = new Forelder.Builder()
            .medPersoninfo(personinfoNorsk)
            .medPersonhistorikkInfo(norskMenUtlandEtÅr)
            .build();
    private static Forelder foreldreIkkeNorskMenBoddNorge = new Forelder.Builder()
            .medPersoninfo(personinfoSvensk)
            .medPersonhistorikkInfo(svenskMenNorgeSeksÅr)
            .build();
    static TpsFakta beggeForeldreOgBarnNorskStatsborger = new TpsFakta.Builder()
            .medForelder(forelderNorsk)
            .medAnnenForelder(forelderNorsk)
            .medBarn(barnNorsk)
            .build();
    static TpsFakta norskOgUtenlandskForelder = new TpsFakta.Builder()
            .medForelder(forelderNorsk)
            .medAnnenForelder(forelderUtland)
            .medBarn(barnNorskMedNorskOgUtenlandskForelder)
            .build();
    static TpsFakta aleneForelderOgBarnNorskStatsborgerskap = new TpsFakta.Builder()
            .medForelder(forelderNorsk)
            .medBarn(barnNorsk)
            .build();
    static TpsFakta beggeForeldreIkkeNorskMenBoddFemINorge = new TpsFakta.Builder()
            .medForelder(foreldreIkkeNorskMenBoddNorge)
            .medAnnenForelder(foreldreIkkeNorskMenBoddNorge)
            .medBarn(barnNorsk)
            .build();
    static TpsFakta beggeForeldreNorskMenBoddIUtland = new TpsFakta.Builder()
            .medForelder(forelderNorskMenBoddIUtland)
            .medAnnenForelder(forelderNorskMenBoddIUtland)
            .medBarn(barnNorsk)
            .build();
    static TpsFakta beggeForeldreOgBarnUtenlandskeStatsborgere = new TpsFakta.Builder()
            .medForelder(forelderUtland)
            .medAnnenForelder(forelderUtland)
            .medBarn(barnUtland)
            .build();

    private static Søknad gradertBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadGradertBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static TpsFakta faktaBeggeForeldreOgBarnNorskStatsborger() {
        return beggeForeldreOgBarnNorskStatsborger;
    }

    public static TpsFakta faktaBeggeForeldreOgBarnUtenlandskeStatsborgere() {
        return beggeForeldreOgBarnUtenlandskeStatsborgere;
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
                .medSøknad(medBarnehageplass())
                .build();
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedTilknytningUtland() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
                .medSøknad(tilknytningUtland())
                .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
                .medSøknad(medBarnehageplass())
                .build();
    }

    public static Faktagrunnlag aleneForelderNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(aleneForelderOgBarnNorskStatsborgerskap)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    public static Faktagrunnlag familieMedEnForelderIUtland() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(norskOgUtenlandskForelder)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    public static Faktagrunnlag beggeForeldreIkkeNorskStatsborgerMenBorINorge() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreIkkeNorskMenBoddFemINorge)
                .medSøknad(utenBarnehageplass())
                .build();
    }

    public static Faktagrunnlag beggeForeldreNorskStatsborgerskapMenBoddIUtland() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(beggeForeldreNorskMenBoddIUtland)
                .medSøknad(medBarnehageplass())
                .build();
    }

    public static Søknad utenBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadUtenBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static Søknad medBarnehageplass() {
        try {
            return mapper.readValue(new File(getFile("soknadFullBarnehageplass.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static Søknad tilknytningUtland() {
        try {
            return mapper.readValue(new File(getFile("soknadTilknytningUtland.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static String getFile(String filnavn) {
        return FaktagrunnlagBuilder.class.getClassLoader().getResource(filnavn).getFile();
    }
}
