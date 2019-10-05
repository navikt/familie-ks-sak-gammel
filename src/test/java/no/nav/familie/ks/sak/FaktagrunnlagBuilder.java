package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Periode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static no.nav.familie.ks.sak.util.Konvertering.konverterTilBoolean;

public final class FaktagrunnlagBuilder {
    private final static Long behandlingId = Long.valueOf("111111111");
    private final static AktørId morAktørId = new AktørId("1300000000001");
    private final static PersonIdent morPersonident = new PersonIdent("00000000001");
    private final static AktørId farAktørId = new AktørId("1300000000002");
    private final static PersonIdent farPersonident = new PersonIdent("00000000002");
    private final static AktørId barnAktørId = new AktørId("1300000000003");
    private final static PersonIdent barnPersonident = new PersonIdent("00000000003");

    public static AktørId norskPersonAktør = new AktørId("00000000001");
    public static PersonIdent norskPersonIdent = new PersonIdent("00000000001");

    private static AktørId utlandForelder = new AktørId("00000000002");
    private static PersonIdent utlandForelderIdent = new PersonIdent("00000000002");

    private static AktørId barnNorskAktørId = new AktørId("11111111111");
    private static PersonIdent barnNorskPersonIdent = new PersonIdent("11111111111");

    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static ObjectMapper mapper = new JacksonJsonConfig().objectMapper();
    private static Personinfo personinfoNorsk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(morAktørId)
        .medPersonIdent(morPersonident)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoNorsk1 = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.NORGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(new AktørId("00000000003"))
        .medPersonIdent(new PersonIdent("00000000003"))
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .build();
    private static Personinfo personinfoSvensk = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.SVERIGE)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utlandForelder)
        .medPersonIdent(utlandForelderIdent)
        .medAdresse("testadresse")
        .medNavn("Svensk Svenskesen")
        .build();
    private static Personinfo personinfoUtland = new Personinfo.Builder()
        .medStatsborgerskap(Landkode.UDEFINERT)
        .medFødselsdato(LocalDate.now().minusYears(30))
        .medAktørId(utlandForelder)
        .medPersonIdent(utlandForelderIdent)
        .medAdresse("annen adresse")
        .medNavn("test testesen")
        .build();
    private static Familierelasjon norskForelderRelasjonMor = new Familierelasjon(
        personinfoNorsk.getAktørId(),
        RelasjonsRolleType.MORA,
        null,
        true);
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
    private final static LocalDate barnNorskFødselsdato = LocalDate.now().minusMonths(13);
    private static PersonMedHistorikk barnNorsk = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(barnNorskAktørId)
        .medPersonIdent(barnNorskPersonIdent)
        .medStatsborgerskap(Landkode.NORGE)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .medFamilierelasjon(new HashSet<>(Collections.singleton(norskForelderRelasjonMor)))
        .build()).medPersonhistorikk(new PersonhistorikkInfo.Builder()
        .medAktørId(
            barnNorskAktørId.getId())
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
        .build();
    private static PersonMedHistorikk barnUtland = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(5))
        .medAktørId(new AktørId("12345678910"))
        .medPersonIdent(new PersonIdent("12345678910"))
        .medStatsborgerskap(Landkode.SVERIGE)
        .medAdresse("svensk adresse")
        .medNavn("test testesen")
        .build()).build();
    private static PersonMedHistorikk barnNorskMedNorskOgUtenlandskForelder = new PersonMedHistorikk.Builder().medInfo(new Personinfo.Builder()
        .medFødselsdato(LocalDate.now().minusMonths(13))
        .medAktørId(new AktørId("12345678910"))
        .medPersonIdent(new PersonIdent("12345678910"))
        .medStatsborgerskap(Landkode.NORGE)
        .medAdresse("testadresse")
        .medNavn("test testesen")
        .medFamilierelasjon(new HashSet<>(Arrays.asList(norskForelderRelasjon, utenlandskForelderRelasjonMedAnnetBosted)))
        .build()).build();
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
    private static PersonMedHistorikk forelderNorsk = new PersonMedHistorikk.Builder()
        .medInfo(personinfoNorsk)
        .medPersonhistorikk(norgeSeksÅr)
        .build();
    private static TpsFakta beggeForeldreOgBarnNorskStatsborger = new TpsFakta.Builder()
        .medForelder(forelderNorsk)
        .medAnnenForelder(forelderNorsk)
        .medBarn(List.of(barnNorsk))
        .build();
    private static TpsFakta aleneForelderOgBarnNorskStatsborgerskap = new TpsFakta.Builder()
        .medForelder(forelderNorsk)
        .medBarn(List.of(barnNorsk))
        .build();
    private static PersonMedHistorikk forelderUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoUtland)
        .medPersonhistorikk(norgeEtÅr)
        .build();
    private static TpsFakta norskOgUtenlandskForelder = new TpsFakta.Builder()
        .medForelder(forelderNorsk)
        .medAnnenForelder(forelderUtland)
        .medBarn(List.of(barnNorskMedNorskOgUtenlandskForelder))
        .build();
    private static TpsFakta beggeForeldreOgBarnUtenlandskeStatsborgere = new TpsFakta.Builder()
        .medForelder(forelderUtland)
        .medAnnenForelder(forelderUtland)
        .medBarn(List.of(barnUtland))
        .build();
    private static PersonMedHistorikk forelderNorskMenBoddIUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoNorsk)
        .medPersonhistorikk(norskMenUtlandEtÅr)
        .build();
    private static PersonMedHistorikk forelder1NorskMenBoddIUtland = new PersonMedHistorikk.Builder()
        .medInfo(personinfoNorsk1)
        .medPersonhistorikk(norskMenUtlandEtÅr)
        .build();
    private static TpsFakta beggeForeldreNorskMenBoddIUtland = new TpsFakta.Builder()
        .medForelder(forelderNorskMenBoddIUtland)
        .medAnnenForelder(forelder1NorskMenBoddIUtland)
        .medBarn(List.of(barnNorsk))
        .build();
    private static PersonMedHistorikk foreldreIkkeNorskMenBoddNorge = new PersonMedHistorikk.Builder()
        .medInfo(personinfoSvensk)
        .medPersonhistorikk(svenskMenNorgeSeksÅr)
        .build();
    private static TpsFakta beggeForeldreIkkeNorskMenBoddFemINorge = new TpsFakta.Builder()
        .medForelder(foreldreIkkeNorskMenBoddNorge)
        .medAnnenForelder(foreldreIkkeNorskMenBoddNorge)
        .medBarn(List.of(barnNorsk))
        .build();

    public static Optional<PersonopplysningGrunnlag> genererPersonopplysningGrunnlag(AktørId annenPartAktørId) {
        PersonopplysningGrunnlag personopplysningGrunnlag = new PersonopplysningGrunnlag(behandlingId);
        return Optional.of(personopplysningGrunnlag);
    }

    private static SøknadGrunnlag genererSøknadGrunnlag(Søknad innsendtSøknad, AktørId søkerAktørId, AktørId annenPartAktørId) {
        final var kravTilSoker = innsendtSøknad.kravTilSoker;
        final var erklæring = new OppgittErklæring(konverterTilBoolean(kravTilSoker.barnIkkeHjemme),
            konverterTilBoolean(kravTilSoker.borSammenMedBarnet),
            konverterTilBoolean(kravTilSoker.ikkeAvtaltDeltBosted),
            konverterTilBoolean(kravTilSoker.skalBoMedBarnetINorgeNesteTolvMaaneder));

        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(innsendtSøknad, søkerAktørId, annenPartAktørId);

        final var innsendtTidspunkt = LocalDateTime.ofInstant(innsendtSøknad.innsendingsTidspunkt, ZoneId.systemDefault());
        return new SøknadGrunnlag(behandlingId, new no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad(innsendtTidspunkt, oppgittUtlandsTilknytning, erklæring));
    }

    private static BarnehageBarnGrunnlag genererBarnehageBarnGrunnlag(Søknad innsendtSøknad) {
        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(Set.of(SøknadTilGrunnlagMapper.mapSøknadBarn(innsendtSøknad).build()));
        familieforholdBuilder.setBorBeggeForeldreSammen(konverterTilBoolean(innsendtSøknad.getFamilieforhold().getBorForeldreneSammenMedBarnet()));
        return new BarnehageBarnGrunnlag(behandlingId, familieforholdBuilder.build());
    }

    public static TpsFakta faktaBeggeForeldreOgBarnNorskStatsborger() {
        return beggeForeldreOgBarnNorskStatsborger;
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(medBarnehageplass(farPersonident.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(medBarnehageplass(farPersonident.getIdent()), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieUtenlandskStatsborgerskapMedTilknytningUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnUtenlandskeStatsborgere)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(tilknytningUtland()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(tilknytningUtland(), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(utenBarnehageplass(farPersonident.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(utenBarnehageplass(farPersonident.getIdent()), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(medBarnehageplass(farPersonident.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(medBarnehageplass(farPersonident.getIdent()), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag familieNorskStatsborgerskapMedGradertBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreOgBarnNorskStatsborger)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag((medGradertBarnehageplass(farPersonident.getIdent()))))
            .medSøknadGrunnlag(genererSøknadGrunnlag(medGradertBarnehageplass(farPersonident.getIdent()), morAktørId, farAktørId))
            .build();
    }

    public static Faktagrunnlag aleneForelderNorskStatsborgerskapUtenBarnehage() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(aleneForelderOgBarnNorskStatsborgerskap)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(utenBarnehageplass("")))
            .medSøknadGrunnlag(genererSøknadGrunnlag(utenBarnehageplass(""), norskPersonAktør, null))
            .build();
    }

    public static Faktagrunnlag familieMedEnForelderIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(norskOgUtenlandskForelder)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(utenBarnehageplass(utlandForelderIdent.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(utenBarnehageplass(utlandForelderIdent.getIdent()), utlandForelder, utlandForelder))
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
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(hentSøknad()))
            .medSøknadGrunnlag(genererSøknadGrunnlag(hentSøknad(), farAktørId, morAktørId))
            .build();
    }

    public static Søknad hentSøknad() {
        final var søknad = utenBarnehageplass(farPersonident.getIdent());
        søknad.getPerson().setFnr(morPersonident.getIdent());
        søknad.getMineBarn().setFødselsnummer(barnPersonident.getIdent());

        return søknad;
    }

    public static Søknad hentSøknadUtenAnnenPart() {
        final var søknad = utenBarnehageplassOgUtenAnnenPart();
        søknad.getPerson().setFnr(morPersonident.getIdent());

        return søknad;
    }

    public static Faktagrunnlag beggeForeldreIkkeNorskStatsborgerMenBorINorge() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreIkkeNorskMenBoddFemINorge)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(utenBarnehageplass(utlandForelderIdent.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(utenBarnehageplass(utlandForelderIdent.getIdent()), utlandForelder, utlandForelder))
            .build();
    }

    public static Faktagrunnlag beggeForeldreNorskStatsborgerskapMenBoddIUtland() {
        return new Faktagrunnlag.Builder()
            .medTpsFakta(beggeForeldreNorskMenBoddIUtland)
            .medBarnehageBarnGrunnlag(genererBarnehageBarnGrunnlag(medBarnehageplass(norskPersonIdent.getIdent())))
            .medSøknadGrunnlag(genererSøknadGrunnlag(medBarnehageplass(norskPersonIdent.getIdent()), norskPersonAktør, norskPersonAktør))
            .build();
    }

    public static Søknad utenBarnehageplassOgUtenAnnenPart() {
        try {
            return mapper.readValue(new File(getFile("soknadUtenBarnehageplassUtenAnnenPart.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static Søknad utenBarnehageplass(String annenPartFnr) {
        try {
            Søknad søknad = mapper.readValue(new File(getFile("soknadUtenBarnehageplass.json")), Søknad.class);
            søknad.getFamilieforhold().setAnnenForelderFødselsnummer(annenPartFnr);
            return søknad;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static Søknad medBarnehageplass(String annenPartFnr) {
        try {
            Søknad søknad = mapper.readValue(new File(getFile("soknadFullBarnehageplass.json")), Søknad.class);
            søknad.getFamilieforhold().setAnnenForelderFødselsnummer(annenPartFnr);
            return søknad;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static Søknad medGradertBarnehageplass(String annenPartFnr) {
        try {
            Søknad søknad = mapper.readValue(new File(getFile("soknadGradertBarnehageplass.json")), Søknad.class);
            søknad.getFamilieforhold().setAnnenForelderFødselsnummer(annenPartFnr);
            return søknad;
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

    public static Søknad medBarnehageplassOgTvillinger() {
        try {
            return mapper.readValue(new File(getFile("soknadFullBarnehageplassTvillinger.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static String getFile(String filnavn) {
        return FaktagrunnlagBuilder.class.getClassLoader().getResource(filnavn).getFile();
    }
}
