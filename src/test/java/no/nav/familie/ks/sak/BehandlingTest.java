package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.grunnlag.Forelder;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
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

public class BehandlingTest {

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_UGYLDIG = false;

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
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaGyldig());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadMedBarnehage.json"));

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaUgyldig());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadMedBarnehage.json"));

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }
/*
    @Test
    public void manuelt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFakta());
        Vedtak vedtak = saksbehandling.behandle(u);

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.UAVKLART);
    }
*/

    private Søknad søknad() {
        try {
            return mapper.readValue(new File(getFile("soknadMedBarnehage.json")), Søknad.class);
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
                .medBarn(barnKsAlderPersoninfo)
                .build();
    }

    private Faktagrunnlag medUgyldigTpsFakta() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaUgyldig())
                .medSøknad(søknad())
                .build();
    }

    private Faktagrunnlag medGyldigTpsFakta() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaGyldig())
                .medSøknad(søknad())
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
