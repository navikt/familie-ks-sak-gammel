package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.GradertPeriode;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.app.behandling.PeriodeOppretter;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.*;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeriodeOppretterTest {

    private static final int MIN_ALDER_I_MÅNEDER = 13;
    private static final int MAKS_ALDER_I_MÅNEDER = 23;
    private static final int MAKS_UTBETALINGSGRAD = 100;

    private static final LocalDate BARNEHAGE_FOM = LocalDate.now().plusMonths(4);
    private static final LocalDate BARNEHAGE_TOM = LocalDate.now().plusYears(3);
    private static final int BARNEHAGE_PROSENT = 65;

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final LocalDate FØDSELSDATO_BARN_UGYLDIG = LocalDate.now().minusMonths(9);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final Saksbehandling saksbehandling = new Saksbehandling();

    private static ObjectMapper mapper =  new ObjectMapper();
    @Before
    public void setUp() {
        saksbehandling.setOppslag(oppslagMock);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void at_søknad_med_barnehage_gir_rett_prosent() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaGyldig());
        var vedtak = saksbehandling.behandle(getFile("soknadMedBarnehage.json"));
        assertThat(vedtak.getStønadperiode().getProsent()).isEqualTo(20);
        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void at_søknad_med_barnehage_gir_rett_periode() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaGyldig());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadMedBarnehage.json"));
        assertThat(vedtak.getStønadperiode().getTom()).isEqualTo(BARNEHAGE_FOM);
    }

    @Test
    public void at_periode_opprettes_korrekt_med_barnehage_og_gyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = medBarnehage();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now());
        assertThat(stønadPeriode.getTom()).isEqualTo(BARNEHAGE_FOM);
        assertThat(stønadPeriode.getProsent()).isEqualTo(20);
    }

    @Test
    public void at_periode_opprettes_korrekt_med_barnehage_og_ugyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = medBarnehageUgyldigAlder();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MIN_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getTom()).isEqualTo(BARNEHAGE_FOM);
        assertThat(stønadPeriode.getProsent()).isEqualTo(20);
    }

    @Test
    public void at_periode_opprettes_korrekt_uten_barnehage_og_gyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = utenBarnehage();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now());
        assertThat(stønadPeriode.getTom()).isEqualTo(FØDSELSDATO_BARN_GYLDIG.plusMonths(MAKS_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    @Test
    public void at_periode_opprettes_korrekt_uten_barnehage_og_ugyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = utenBarnehageUgyldigAlder();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MIN_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getTom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MAKS_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
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
                .medBarn(barnKsAlderPersoninfo)
                .build();
    }

    private Faktagrunnlag utenBarnehage() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknad())
                .medTpsFakta(tpsFaktaGyldig())
                .build();
    }

    private Faktagrunnlag medBarnehage() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknad())
                .medTpsFakta(tpsFaktaGyldig())
                .build();
    }

    private Faktagrunnlag medBarnehageUgyldigAlder() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknad())
                .medTpsFakta(tpsFaktaGyldig())
                .build();
    }

    private Faktagrunnlag utenBarnehageUgyldigAlder() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknad())
                .medTpsFakta(tpsFaktaUgyldig())
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
            .medFødselsdato(LocalDate.now().minusMonths(13))
            .medAktørId(new AktørId("12345678910"))
            .medPersonIdent(new PersonIdent("12345678910"))
            .medAdresse("testadresse")
            .medNavn("test testesen")
            .build();

    private PersonhistorikkInfo femÅrPersonInfoHistorikk = new PersonhistorikkInfo.Builder()
            .medPersonIdent("12345678910")
            .leggTil(norskAdresseEtÅr)
            .build();

    private PersonhistorikkInfo mindreEnnFemÅrPersonInfoHistorikk = new PersonhistorikkInfo.Builder()
            .medPersonIdent("12345678910")
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


    private Søknad søknad() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehage.json")), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
