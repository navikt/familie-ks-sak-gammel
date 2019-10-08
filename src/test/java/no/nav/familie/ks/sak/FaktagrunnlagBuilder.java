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
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Periode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;

import java.time.LocalDate;
import java.util.*;

public final class FaktagrunnlagBuilder {
    private final static Long behandlingId = Long.valueOf("111111111");

    public final static AktørId morAktørId = new AktørId(SøknadTestdata.morAktørId);
    public final static PersonIdent morPersonident = new PersonIdent(SøknadTestdata.morPersonident);

    public final static AktørId farAktørId = new AktørId(SøknadTestdata.farAktørId);
    public final static PersonIdent farPersonident = new PersonIdent(SøknadTestdata.farPersonident);

    public final static AktørId barnAktørId = new AktørId(SøknadTestdata.barnAktørId);
    public final static PersonIdent barnPersonident = new PersonIdent(SøknadTestdata.barnPersonident);

    public final static AktørId utenlandskBarnAktørId = new AktørId(SøknadTestdata.utenlandskBarnAktørId);
    public final static PersonIdent utenlandskBarnPersonident = new PersonIdent(SøknadTestdata.utenlandskBarnPersonident);

    public static AktørId utenlandskMorAktørId = new AktørId(SøknadTestdata.utenlandskMorAktørId);
    public static PersonIdent utenlandskMorPersonident = new PersonIdent(SøknadTestdata.utenlandskMorPersonident);

    public static AktørId utenlandskFarAktørId = new AktørId(SøknadTestdata.utenlandskFarAktørId);
    public static PersonIdent utenlandskFarPersonident = new PersonIdent(SøknadTestdata.utenlandskFarPersonident);

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";

    // Personinfo
    private static Personinfo personinfoMorNorsk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(morAktørId)
        .medPersonIdent(morPersonident)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoFarNorsk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(farAktørId)
        .medPersonIdent(farPersonident)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoSvenskMor = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.SVERIGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utenlandskMorAktørId)
        .medPersonIdent(utenlandskMorPersonident)
        .medAdresse("testadresse")
        .medNavn("Svensk Svenskesen")
        .build();
    private static Personinfo personinfoSvenskFar = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.SVERIGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utenlandskFarAktørId)
        .medPersonIdent(utenlandskFarPersonident)
        .medAdresse("annen adresse")
        .medNavn("test testesen")
        .build();

    // Familierelasjoner
    private static Familierelasjon norskForelderRelasjonMor = new Familierelasjon(
        personinfoMorNorsk.getAktørId(),
        RelasjonsRolleType.MORA,
        null,
        true);
    private static Familierelasjon norskForelderRelasjonFar = new Familierelasjon(
        personinfoFarNorsk.getAktørId(),
        RelasjonsRolleType.FARA,
        null,
        true);
    private static Familierelasjon utenlandskMorRelasjonMedAnnetBosted = new Familierelasjon(
        personinfoSvenskMor.getAktørId(),
        RelasjonsRolleType.MORA,
        null,
        false);
    private static Familierelasjon utenlandskFarRelasjonMedAnnetBosted = new Familierelasjon(
        personinfoSvenskFar.getAktørId(),
        RelasjonsRolleType.FARA,
        null,
        false);


    // Adresseperioder
    private static AdressePeriode norskAdresseSeksÅr = new AdressePeriode.Builder()
        .medLand(STATSBORGERSKAP_GYLDIG)
        .medAdresseType(AdresseType.BOSTEDSADRESSE)
        .medGyldighetsperiode(
            new Periode(LocalDate.now().minusYears(6), Tid.TIDENES_ENDE)
        )
        .build();
    private static AdressePeriode svenskdresseSeksÅr = new AdressePeriode.Builder()
        .medLand(Landkode.SVERIGE.getKode())
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
        .medAktørId(morAktørId.getId())
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkFarNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medAktørId(farAktørId.getId())
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkBarnNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medAktørId(barnAktørId.getId())
        .leggTil(norskAdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskMorNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medAktørId(utenlandskMorAktørId.getId())
        .leggTil(norskAdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskFarNorgeSeksÅr = new PersonhistorikkInfo.Builder()
        .medAktørId(utenlandskFarAktørId.getId())
        .leggTil(norskAdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskMor = new PersonhistorikkInfo.Builder()
        .medAktørId(utenlandskMorAktørId.getId())
        .leggTil(svenskdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkSvenskFar = new PersonhistorikkInfo.Builder()
        .medAktørId(utenlandskFarAktørId.getId())
        .leggTil(svenskdresseSeksÅr)
        .leggTil(svenskStatsborger)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskMorUtlandskAdresseNorskStatsborger = new PersonhistorikkInfo.Builder()
        .medAktørId(morAktørId.getId())
        .leggTil(svenskdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskFarUtlandskAdresseNorskStatsborger = new PersonhistorikkInfo.Builder()
        .medAktørId(farAktørId.getId())
        .leggTil(svenskdresseSeksÅr)
        .leggTil(norskStatsborgerskapSeksÅr)
        .build();
    private static PersonhistorikkInfo personhistorikkNorskFarNorgeEtÅr = new PersonhistorikkInfo.Builder()
        .medAktørId(farAktørId.getId())
        .leggTil(norskAdresseEtÅr)
        .leggTil(norskStatsborgerskapEtÅr)
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

    private final static LocalDate barnNorskFødselsdato = LocalDate.now().minusMonths(13);
    private static PersonMedHistorikk personMedHistorikkBarnNorsk = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .medFamilierelasjon(new HashSet<>(List.of(norskForelderRelasjonMor, norskForelderRelasjonFar)))
        .build()).medPersonhistorikk(new PersonhistorikkInfo.Builder()
        .medAktørId(
            barnAktørId.getId())
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
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .medFamilierelasjon(new HashSet<>(List.of(norskForelderRelasjonMor)))
        .build()).medPersonhistorikk(new PersonhistorikkInfo.Builder()
        .medAktørId(
            barnAktørId.getId())
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
        .medAdresse("svensk adresse")
        .medNavn("test testesen")
        .build()).build();
    private static PersonMedHistorikk personMedHistorikkBarnNorskMedUtenlandskForeldre = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .medFamilierelasjon(new HashSet<>(Arrays.asList(utenlandskMorRelasjonMedAnnetBosted, utenlandskFarRelasjonMedAnnetBosted)))
        .build()).build();
    private static PersonMedHistorikk personMedHistorikkBarnNorskMedNorskOgUtenlandskForelder = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnAktørId)
        .medPersonIdent(barnPersonident)
        .medStatsborgerskap(Landkode.NORGE)
        .medAdresse("testadresse")
        .medNavn("test testesen")
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
    
    private static TpsFakta beggeForeldreIkkeNorskMenBoddFemINorge = new TpsFakta.Builder()
        .medForelder(personMedHistorikkMorIkkeNorskMenBoddNorge)
        .medAnnenForelder(personMedHistorikkMorIkkeNorskMenBoddNorge)
        .medBarn(List.of(personMedHistorikkBarnNorsk))
        .build();

    public static Optional<PersonopplysningGrunnlag> genererPersonopplysningGrunnlag(AktørId annenPartAktørId) {
        PersonopplysningGrunnlag personopplysningGrunnlag = new PersonopplysningGrunnlag(behandlingId);
        return Optional.of(personopplysningGrunnlag);
    }

    private static SøknadGrunnlag genererSøknadGrunnlag(Søknad innsendtSøknad, AktørId søkerAktørId, AktørId annenPartAktørId) {
        final var kravTilSoker = innsendtSøknad.getOppgittErklæring();
        final var erklæring = new OppgittErklæring(kravTilSoker.isBarnetHjemmeværendeOgIkkeAdoptert(),
            kravTilSoker.isBorSammenMedBarnet(),
            kravTilSoker.isIkkeAvtaltDeltBosted(),
            kravTilSoker.isBarnINorgeNeste12Måneder());

        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(innsendtSøknad, søkerAktørId, annenPartAktørId);

        return new SøknadGrunnlag(behandlingId, new no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad(innsendtSøknad.getInnsendtTidspunkt(), oppgittUtlandsTilknytning, erklæring));
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
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.utenlandskFamilieMedBarnehageplass(), utenlandskMorAktørId, utenlandskFarAktørId))
            .build();
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedTilknytningUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.tilknytningUtlandUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.tilknytningUtlandUtenBarnehageplass(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass(), farAktørId, morAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehageFlerlinger() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgFlerlingerNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplassFlerlinger()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplassFlerlinger(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieMedBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieMedBarnehageplass(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedGradertBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag((SøknadTestdata.norskFamilieGradertBarnehageplass())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieGradertBarnehageplass(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag aleneForelderNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(aleneForelderOgBarnNorskStatsborgerskap)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass(), morAktørId, null))
            .build();
    }

    public static Faktagrunnlag familieMedEnForelderIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(norskOgUtenlandskForelder)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.enForelderIUtlandUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.enForelderIUtlandUtenBarnehageplass(), morAktørId, utenlandskFarAktørId))
            .build();
    }

    public static Faktagrunnlag beggeForeldreIkkeNorskStatsborgerMenBorINorge() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreIkkeNorskMenBoddFemINorge)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag beggeForeldreNorskStatsborgerskapMenBoddIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreNorskMenBoddIUtland)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass(), utenlandskMorAktørId, utenlandskFarAktørId))
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
                    .medPersonIdent(morPersonident)
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(barnAktørId, RelasjonsRolleType.BARN, barnFødselsdato, true),
                        new Familierelasjon(farAktørId, RelasjonsRolleType.EKTE, farFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medAktørId(morAktørId.getId())
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
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(barnAktørId, RelasjonsRolleType.BARN, barnFødselsdato, true),
                        new Familierelasjon(morAktørId, RelasjonsRolleType.EKTE, morFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medAktørId(farAktørId.getId())
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
                    .medStatsborgerskap(Landkode.NORGE)
                    .medFamilierelasjon(Set.of(
                        new Familierelasjon(farAktørId, RelasjonsRolleType.FARA, farFødselsdato, true),
                        new Familierelasjon(morAktørId, RelasjonsRolleType.MORA, morFødselsdato, true)))
                    .build())
                .medPersonhistorikk(new PersonhistorikkInfo.Builder()
                    .medAktørId(barnAktørId.getId())
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
            .medSøknadGrunnlag(genererSøknadGrunnlag(SøknadTestdata.norskFamilieUtenBarnehageplass(), morAktørId, farAktørId))
            .build();
    }
}
