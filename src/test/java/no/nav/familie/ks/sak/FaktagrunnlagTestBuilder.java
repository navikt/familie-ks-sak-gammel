package no.nav.familie.ks.sak;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.SøknadTilGrunnlagMapper;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittErklæring;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.Tid;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.MedlFakta;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.PeriodeInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.PeriodeStatus;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Periode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.Adresseinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;

import java.time.LocalDate;
import java.util.*;

public final class FaktagrunnlagTestBuilder {
    private final static Long behandlingId = Long.valueOf("111111111");

    private final static AktørId morAktørId = new AktørId(SøknadTestdata.morAktørId);
    private final static PersonIdent morPersonident = new PersonIdent(SøknadTestdata.morPersonident);

    private final static AktørId farAktørId = new AktørId(SøknadTestdata.farAktørId);
    private final static PersonIdent farPersonident = new PersonIdent(SøknadTestdata.farPersonident);

    private final static AktørId barnAktørId = new AktørId(SøknadTestdata.barnAktørId);
    private final static PersonIdent barnPersonident = new PersonIdent(SøknadTestdata.barnPersonident);

    private final static AktørId utenlandskBarnAktørId = new AktørId(SøknadTestdata.utenlandskBarnAktørId);
    private final static PersonIdent utenlandskBarnPersonident = new PersonIdent(SøknadTestdata.utenlandskBarnPersonident);

    private static AktørId utenlandskMorAktørId = new AktørId(SøknadTestdata.utenlandskMorAktørId);
    private static PersonIdent utenlandskMorPersonident = new PersonIdent(SøknadTestdata.utenlandskMorPersonident);

    private static AktørId utenlandskFarAktørId = new AktørId(SøknadTestdata.utenlandskFarAktørId);
    private static PersonIdent utenlandskFarPersonident = new PersonIdent(SøknadTestdata.utenlandskFarPersonident);

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";

    // Personinfo
    private static Personinfo personinfoMorNorsk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(morAktørId)
        .medKjønn("KVINNE")
        .medPersonIdent(morPersonident)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoFarNorsk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(farAktørId)
        .medPersonIdent(farPersonident)
        .medKjønn("MANN")
        .medPersonIdent(farPersonident)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoFarNorskStemmerDelvis = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(farAktørId)
        .medPersonIdent(morPersonident)
        .medKjønn("MANN")
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoFarNorskStemmerIkke = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(farAktørId)
        .medPersonIdent(new PersonIdent("00000000011"))
        .medKjønn("MANN")
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoSvenskMor = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.SVERIGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utenlandskMorAktørId)
        .medKjønn("KVINNE")
        .medPersonIdent(utenlandskMorPersonident)
        .medBostedsadresse(medBostedsadresse("SWE"))
        .medNavn("Svensk Svenskesen")
        .build();
    private static Personinfo personinfoSvenskFar = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.SVERIGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utenlandskFarAktørId)
        .medKjønn("MANN")
        .medPersonIdent(utenlandskFarPersonident)
        .medBostedsadresse(medBostedsadresse("SWE"))
        .medNavn("test testesen")
        .build();

    // Familierelasjoner
    private static Familierelasjon norskForelderRelasjonMor = new Familierelasjon(
        personinfoMorNorsk.getPersonIdent(),
        RelasjonsRolleType.MORA,
        null,
        true);
    private static Familierelasjon norskForelderRelasjonFar = new Familierelasjon(
        personinfoFarNorsk.getPersonIdent(),
        RelasjonsRolleType.FARA,
        null,
        true);
    private static Familierelasjon utenlandskMorRelasjonMedAnnetBosted = new Familierelasjon(
        personinfoSvenskMor.getPersonIdent(),
        RelasjonsRolleType.MORA,
        null,
        false);
    private static Familierelasjon utenlandskFarRelasjonMedAnnetBosted = new Familierelasjon(
        personinfoSvenskFar.getPersonIdent(),
        RelasjonsRolleType.FARA,
        null,
        false);


    // Adresseperioder
    private static AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder()
        .medLand(STATSBORGERSKAP_GYLDIG)
        .medAdresselinje1("adresselinje1")
        .medPostnummer("1234")
        .medPoststed("Oslo")
        .medAdresseType(AdresseType.BOSTEDSADRESSE)
        .medGyldighetsperiode(
            new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE)
        )
        .build();
    private static AdressePeriode svenskdresseSeksÅr = new AdressePeriode.Builder()
        .medLand(Landkode.SVERIGE.getKode())
        .medAdresselinje1("adresselinje1")
        .medPostnummer("1234")
        .medPoststed("Strømstad")
        .medAdresseType(AdresseType.BOSTEDSADRESSE)
        .medGyldighetsperiode(
            new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE)
        )
        .build();
    private static AdressePeriode norskAdresseEtÅr = new AdressePeriode.Builder()
        .medLand(STATSBORGERSKAP_GYLDIG)
        .medAdresselinje1("adresselinje1")
        .medPostnummer("1234")
        .medPoststed("Oslo")
        .medLand(Landkode.NORGE.getKode())
        .medAdresseType(AdresseType.BOSTEDSADRESSE)
        .medGyldighetsperiode(
            new Periode(LocalDate.now().minusYears(1), Tid.TIDENES_ENDE)
        )
        .build();

    // Statsborgerskapperioder
    private static StatsborgerskapPeriode norskStatsborgerskapSeksÅr = new StatsborgerskapPeriode(
        new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE),
        Landkode.NORGE);
    private static StatsborgerskapPeriode svenskStatsborger = new StatsborgerskapPeriode(
        new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE),
        Landkode.SVERIGE);
    private static StatsborgerskapPeriode norskStatsborgerskapEtÅr = new StatsborgerskapPeriode(
        new Periode(LocalDate.now().minusYears(1), Tid.TIDENES_ENDE),
        Landkode.NORGE);

    // Personhistorikk
    private static PersonhistorikkInfo personhistorikkMorNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(morPersonident)
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkFarNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(farPersonident)
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkBarnNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(barnPersonident)
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskMorNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(utenlandskMorPersonident)
        .leggTil(norskAdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskFarNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(utenlandskFarPersonident)
        .leggTil(norskAdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskMor = new PersonhistorikkInfo.Builder()
        .medPersonIdent(utenlandskMorPersonident)
        .leggTil(svenskdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskFar = new PersonhistorikkInfo.Builder()
        .medPersonIdent(utenlandskFarPersonident)
        .leggTil(svenskdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskMorUtlandskAdresseNorskStatsborger = new PersonhistorikkInfo.Builder()
        .medPersonIdent(morPersonident)
        .leggTil(svenskdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskFarUtlandskAdresseNorskStatsborger = new PersonhistorikkInfo.Builder()
        .medPersonIdent(farPersonident)
        .leggTil(svenskdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskFarNorgeEtÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(farPersonident)
        .leggTil(norskAdresseEtÅr)
        .leggTil(norskStatsborgerskapEtÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskMorNorgeEtÅr = new PersonhistorikkInfo.Builder()
        .medPersonIdent(morPersonident)
        .leggTil(norskAdresseEtÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();

    // Personer med historikk
    private static PersonMedHistorikk personMedHistorikkMorNorsk = new PersonMedHistorikk.Builder()
        .medInfo(personinfoMorNorsk)
        .medPersonhistorikk(personhistorikkMorNorgeSeksÅr)
        .build();
    private static PersonMedHistorikk personMedHistorikkFarNorsk = new PersonMedHistorikk.Builder()
        .medInfo(personinfoFarNorsk)
        .medPersonhistorikk(personhistorikkFarNorgeSeksÅr)
        .build();
    private static PersonMedHistorikk personMedHistorikkFarNorskStemmerDelvis = new PersonMedHistorikk.Builder()
        .medInfo(personinfoFarNorskStemmerDelvis)
        .medPersonhistorikk(personhistorikkFarNorgeSeksÅr)
        .build();
    private static PersonMedHistorikk personMedHistorikkFarNorskStemmerIkke = new PersonMedHistorikk.Builder()
        .medInfo(personinfoFarNorskStemmerIkke)
        .medPersonhistorikk(personhistorikkFarNorgeSeksÅr)
        .build();
    private static PersonMedHistorikk personMedHistorikkMorNorskMenIkkeBoddFemÅrINorge = new PersonMedHistorikk.Builder()
        .medInfo(personinfoMorNorsk)
        .medPersonhistorikk(personhistorikkNorskMorNorgeEtÅr)
        .build();

    private final static LocalDate barnNorskFødselsdato = LocalDate.now().minusMonths(13);
    private static PersonMedHistorikk personMedHistorikkBarnNorsk = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .medKjønn("MANN")
        .medFamilierelasjon(new HashSet<>(List.of(norskForelderRelasjonMor, norskForelderRelasjonFar)))
        .build()).medPersonhistorikk(new PersonhistorikkInfo.Builder()
        .medPersonIdent(barnPersonident)
        .leggTil(AdressePeriode.builder()
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medAdresselinje1("Svingen")
            .medPostnummer("0001")
            .medPoststed("Oslo")
            .medLand(no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode.NORGE.getKode())
            .medGyldighetsperiode(Periode.fraTilTidenesEnde(barnNorskFødselsdato))
            .build())
        .leggTil(new StatsborgerskapPeriode(Periode.fraTilTidenesEnde(barnNorskFødselsdato), Landkode.NORGE))
        .build())
        .medPersonhistorikk(personhistorikkBarnNorgeSeksÅr)
        .build();

    private static PersonMedHistorikk personMedHistorikkBarnNorskEnForelder = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .medKjønn("MANN")
        .medFamilierelasjon(new HashSet<>(List.of(norskForelderRelasjonMor)))
        .build()).medPersonhistorikk(new PersonhistorikkInfo.Builder()
        .medPersonIdent(barnPersonident)
        .leggTil(AdressePeriode.builder()
            .medAdresseType(AdresseType.BOSTEDSADRESSE)
            .medAdresselinje1("Svingen")
            .medPostnummer("0001")
            .medPoststed("Oslo")
            .medLand(no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode.NORGE.getKode())
            .medGyldighetsperiode(Periode.fraTilTidenesEnde(barnNorskFødselsdato))
            .build())
        .leggTil(new StatsborgerskapPeriode(Periode.fraTilTidenesEnde(barnNorskFødselsdato), Landkode.NORGE))
        .build())
        .medPersonhistorikk(personhistorikkBarnNorgeSeksÅr)
        .build();
    private static PersonMedHistorikk personMedHistorikkBarnUtland = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(5))
        .medAktørId(utenlandskBarnAktørId)
        .medPersonIdent(utenlandskBarnPersonident)
        .medStatsborgerskap(Landkode.SVERIGE)
        .medBostedsadresse(medBostedsadresse("SWE"))
        .medNavn("test testesen")
        .medKjønn("MANN")
        .build()).build();
    private static PersonMedHistorikk personMedHistorikkBarnNorskMedUtenlandskForeldre = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .medKjønn("MANN")
        .medFamilierelasjon(new HashSet<>(Arrays.asList(utenlandskMorRelasjonMedAnnetBosted, utenlandskFarRelasjonMedAnnetBosted)))
        .build()).build();
    private static PersonMedHistorikk personMedHistorikkBarnNorskMedNorskOgUtenlandskForelder = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medBostedsadresse(medBostedsadresse("NOR"))
        .medNavn("test testesen")
        .medKjønn("MANN")
        .medFamilierelasjon(new HashSet<>(Arrays.asList(norskForelderRelasjonFar, utenlandskMorRelasjonMedAnnetBosted)))
        .build()).build();

    private static PersonMedHistorikk personMedHistorikkMorUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoSvenskMor)
        .medPersonhistorikk(personhistorikkSvenskMor)
        .build();

    private static PersonMedHistorikk personMedHistorikkFarUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoSvenskFar)
        .medPersonhistorikk(personhistorikkSvenskFar)
        .build();

    private static PersonMedHistorikk PersonMedHistorikkMorNorskMenBoddIUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoMorNorsk)
        .medPersonhistorikk(personhistorikkNorskMorUtlandskAdresseNorskStatsborger)
        .build();
    private static PersonMedHistorikk personMedHistorikkFarNorskMenBoddIUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoFarNorsk)
        .medPersonhistorikk(personhistorikkNorskFarUtlandskAdresseNorskStatsborger)
        .build();

    private static PersonMedHistorikk personMedHistorikkMorIkkeNorskMenBoddNorge = new PersonMedHistorikk.Builder()
        .medInfo(personinfoSvenskMor)
        .medPersonhistorikk(personhistorikkSvenskMorNorgeSeksÅr)
        .build();

    // Tps fakta
    private static TpsFakta beggeForeldreOgBarnNorskStatsborger = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medAnnenForelder(personMedHistorikkFarNorsk)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();
    private static TpsFakta beggeForeldreOgBarnNorskStatsborgerAnnenPartStemmerDelvis = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medAnnenForelder(personMedHistorikkFarNorskStemmerDelvis)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();
    private static TpsFakta beggeForeldreOgBarnNorskStatsborgerAnnenPartStemmerIkke = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medAnnenForelder(personMedHistorikkFarNorskStemmerIkke)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();
    private static TpsFakta beggeForeldreOgFlerlingerNorskStatsborger = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medAnnenForelder(personMedHistorikkFarNorsk)
        .medBarn(List.of(personMedHistorikkBarnNorsk, personMedHistorikkBarnNorsk))
        .build();
    private static TpsFakta aleneForelderOgBarnNorskStatsborgerskap = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medBarn(List.of(personMedHistorikkBarnNorskEnForelder))
        .build();

    private static TpsFakta norskOgUtenlandskForelder = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorsk)
        .medAnnenForelder(personMedHistorikkFarUtland)
        .medBarn(List.of(personMedHistorikkBarnNorskMedNorskOgUtenlandskForelder))
        .build();
    private static TpsFakta beggeForeldreOgBarnUtenlandskeStatsborgere = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorUtland)
        .medAnnenForelder(personMedHistorikkFarUtland)
        .medBarn(List.of(personMedHistorikkBarnUtland))
        .build();

    private static TpsFakta beggeForeldreNorskMenBoddIUtland = new TpsFakta.Builder()
        .medForelder(PersonMedHistorikkMorNorskMenBoddIUtland)
        .medAnnenForelder(personMedHistorikkFarNorskMenBoddIUtland)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();

    private static TpsFakta beggeForeldreNorskMenIkkeBoddFemÅrINorge = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorNorskMenIkkeBoddFemÅrINorge)
        .medAnnenForelder(personMedHistorikkFarNorsk)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();

    private static TpsFakta beggeForeldreIkkeNorskMenBoddFemINorge = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorIkkeNorskMenBoddNorge)
        .medAnnenForelder(personMedHistorikkMorIkkeNorskMenBoddNorge)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();

    public static Optional<PersonopplysningGrunnlag> genererPersonopplysningGrunnlag() {
        PersonopplysningGrunnlag personopplysningGrunnlag = new PersonopplysningGrunnlag(behandlingId);
        return Optional.of(personopplysningGrunnlag);
    }

    // Medlemskapsinformasjon
    public static PeriodeInfo gyldigPeriode = new PeriodeInfo.Builder()
        .medPeriodeStatus(PeriodeStatus.GYLD)
        .medFom(LocalDate.now().minusYears(4))
        .medTom(LocalDate.now().minusYears(3))
        .medGjelderMedlemskapIFolketrygden(false)
        .medGrunnlag("FO_12_1")
        .medDekning("Full")
        .build();

    public static MedlemskapsInfo søker = new MedlemskapsInfo.Builder()
        .medPersonIdent(morPersonident.getIdent())
        .medGyldigePerioder(Arrays.asList(gyldigPeriode))
        .build();

    public static MedlemskapsInfo annenForelder = new MedlemskapsInfo.Builder()
        .medPersonIdent(farPersonident.getIdent())
        .medGyldigePerioder(Arrays.asList(gyldigPeriode))
        .build();

    public static MedlFakta treffIMedlBeggeForeldrene = new MedlFakta.Builder()
        .medSøker(Optional.of(søker))
        .medAnnenForelder(Optional.of(annenForelder))
        .build();

    public static MedlFakta ingenMedlemsopplysninger = new MedlFakta.Builder()
        .medSøker(Optional.empty())
        .medAnnenForelder(Optional.empty())
        .build();

    public static MedlemskapsInfo tomMedlemskapsinfo() {
        return new MedlemskapsInfo.Builder()
            .medPersonIdent(null)
            .medGyldigePerioder(null)
            .build();
    }

    public static Adresseinfo medBostedsadresse(String landkode) {
        return new Adresseinfo.Builder(AdresseType.BOSTEDSADRESSE, "test testesen", PersonstatusType.BOSA)
            .medAdresselinje1("Svingen")
            .medPostNr("0001")
            .medPoststed("Oslo")
            .medLand(landkode)
            .build();
    }

    private static SøknadGrunnlag genererSøknadGrunnlag(Søknad innsendtSøknad) {
        final var kravTilSoker = innsendtSøknad.getOppgittErklæring();
        final var erklæring = new OppgittErklæring(kravTilSoker.isBarnetHjemmeværendeOgIkkeAdoptert(),
            kravTilSoker.isBorSammenMedBarnet(),
            kravTilSoker.isIkkeAvtaltDeltBosted(),
            kravTilSoker.isBarnINorgeNeste12Måneder());

        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(innsendtSøknad);

        return new SøknadGrunnlag(behandlingId, new no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad(innsendtSøknad.getInnsendtTidspunkt(), innsendtSøknad.getSøkerFødselsnummer(), innsendtSøknad.getOppgittAnnenPartFødselsnummer(), oppgittUtlandsTilknytning, erklæring));
    }

    private static BarnehageBarnGrunnlag genererBarnehageBarnGrunnlag(Søknad innsendtSøknad) {
        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();

        familieforholdBuilder.setBarna(SøknadTilGrunnlagMapper.mapSøknadBarn(innsendtSøknad));
        familieforholdBuilder.setBorBeggeForeldreSammen(innsendtSøknad.getOppgittFamilieforhold().getBorBeggeForeldreSammen());
        return new BarnehageBarnGrunnlag(behandlingId, familieforholdBuilder.build());
    }

    public static TpsFakta faktaBeggeForeldreOgBarnNorskStatsborger() {
        return beggeForeldreOgBarnNorskStatsborger;
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.utenlandskFamilieMedBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.utenlandskFamilieMedBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedMedlemskapsinfo() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(treffIMedlBeggeForeldrene)
            .build();
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedTilknytningUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.tilknytningUtlandUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.tilknytningUtlandUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehageAnnenPartStemmerDelvis() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborgerAnnenPartStemmerDelvis)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehageAnnenPartStemmerIkke() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborgerAnnenPartStemmerIkke)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehageFlerlinger() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgFlerlingerNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplassFlerlinger()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplassFlerlinger()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieMedBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieMedBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedGradertBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag((SøknadTestdata.norskFamilieGradertBarnehageplass())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieGradertBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag aleneForelderNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(aleneForelderOgBarnNorskStatsborgerskap)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag familieMedEnForelderIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(norskOgUtenlandskForelder)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.enForelderIUtlandUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.enForelderIUtlandUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag beggeForeldreIkkeNorskStatsborgerMenBorINorge() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreIkkeNorskMenBoddFemINorge)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag beggeForeldreNorskStatsborgerskapMenBoddIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreNorskMenBoddIUtland)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag beggeForeldreNorskStatsborgerskapMenBoddINorgeMindreEnnFemÅr() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreNorskMenIkkeBoddFemÅrINorge)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }

    public static Faktagrunnlag beggeForeldreBorINorgeOgErNorskeStatsborgere() {
        final var morFødselsdato = LocalDate.now().minusYears(30);
        final var farFødselsdato = LocalDate.now().minusYears(33);
        final var barnFødselsdato = LocalDate.now().minusMonths(13);
        final var fakta = new TpsFakta.Builder()
            .medForelder(new PersonMedHistorikk.Builder()
                .medInfo(new Personinfo.Builder()
                    .medAktørId(morAktørId)
                    .medFødselsdato(morFødselsdato)
                    .medNavn("Kari Nordmann")
                    .medKjønn("KVINNE")
                    .medPersonIdent(morPersonident)
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(barnPersonident, RelasjonsRolleType.BARN, barnFødselsdato, true),
                        new Familierelasjon(farPersonident, RelasjonsRolleType.EKTE, farFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medPersonIdent(morPersonident)
                    .leggTil(AdressePeriode.builder()
                        .medAdresseType(AdresseType.BOSTEDSADRESSE)
                        .medAdresselinje1("Svingen")
                        .medPostnummer("0001")
                        .medPoststed("Oslo")
                        .medLand(no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode.NORGE.getKode())
                        .medGyldighetsperiode(Periode.fraTilTidenesEnde(morFødselsdato))
                        .build())
                    .leggTil(new StatsborgerskapPeriode(Periode.fraTilTidenesEnde(morFødselsdato), Landkode.NORGE))
                    .build())
                .build())
            .medAnnenForelder(new PersonMedHistorikk.Builder()
                .medInfo(new Personinfo.Builder()
                    .medAktørId(farAktørId)
                    .medFødselsdato(farFødselsdato)
                    .medPersonIdent(farPersonident)
                    .medNavn("Ola Nordmann")
                    .medKjønn("MANN")
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(barnPersonident, RelasjonsRolleType.BARN, barnFødselsdato, true),
                        new Familierelasjon(morPersonident, RelasjonsRolleType.EKTE, morFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medPersonIdent(farPersonident)
                    .leggTil(AdressePeriode.builder()
                        .medAdresseType(AdresseType.BOSTEDSADRESSE)
                        .medAdresselinje1("Svingen")
                        .medPostnummer("0001")
                        .medPoststed("Oslo")
                        .medLand(no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode.NORGE.getKode())
                        .medGyldighetsperiode(Periode.fraTilTidenesEnde(farFødselsdato))
                        .build())
                    .leggTil(new StatsborgerskapPeriode(Periode.fraTilTidenesEnde(farFødselsdato), Landkode.NORGE))
                    .build())
                .build())
            .medBarn(List.of(new PersonMedHistorikk.Builder()
                .medInfo(new Personinfo.Builder()
                    .medAktørId(barnAktørId)
                    .medFødselsdato(barnFødselsdato)
                    .medPersonIdent(barnPersonident)
                    .medNavn("Espen Askeladd")
                    .medKjønn("MANN")
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(farPersonident, RelasjonsRolleType.FARA, farFødselsdato, true),
                        new Familierelasjon(morPersonident, RelasjonsRolleType.MORA, morFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medPersonIdent(barnPersonident)
                    .leggTil(AdressePeriode.builder()
                        .medAdresseType(AdresseType.BOSTEDSADRESSE)
                        .medAdresselinje1("Svingen")
                        .medPostnummer("0001")
                        .medPoststed("Oslo")
                        .medLand(no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode.NORGE.getKode())
                        .medGyldighetsperiode(Periode.fraTilTidenesEnde(barnFødselsdato))
                        .build())
                    .leggTil(new StatsborgerskapPeriode(Periode.fraTilTidenesEnde(barnFødselsdato), Landkode.NORGE))
                    .build())
                .build()))
            .build();


        return new Faktagrunnlag.Builder()
            .medTpsFakta(fakta)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medMedlFakta(ingenMedlemsopplysninger)
            .build();
    }
}
