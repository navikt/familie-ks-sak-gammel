package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningService;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ApplicationConfig.class},
    loader = AnnotationConfigContextLoader.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
public class RegisterInnhentingServiceTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;
    @MockBean
    private StsRestClient stsRestClient;
    @Autowired
    private RegisterInnhentingService tjeneste;
    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private PersonopplysningService personopplysningService;
    @Autowired
    private BehandlingRepository behandlingRepository;

    private final Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreBorINorgeOgErNorskeStatsborgere();
    private final TpsFakta tpsFakta = faktagrunnlag.getTpsFakta();

    private AktørId søker = tpsFakta.getForelder().getPersoninfo().getAktørId();
    private Personinfo søkerPersoninfo = tpsFakta.getForelder().getPersoninfo();
    private PersonhistorikkInfo søkerPersonhistorikk = tpsFakta.getForelder().getPersonhistorikkInfo();

    private AktørId annenPart = tpsFakta.getAnnenForelder().getPersoninfo().getAktørId();
    private Personinfo annenPartPersoninfo = tpsFakta.getAnnenForelder().getPersoninfo();
    private PersonhistorikkInfo annenPartPersonhistorikk = tpsFakta.getAnnenForelder().getPersonhistorikkInfo();
    private AktørId barn = tpsFakta.getBarna().get(0).getPersoninfo().getAktørId();
    private Personinfo barnPersoninfo = tpsFakta.getBarna().get(0).getPersoninfo();
    private PersonhistorikkInfo barnPersonhistorikk = tpsFakta.getBarna().get(0).getPersonhistorikkInfo();

    @Before
    public void setUp() {
        when(oppslagTjeneste.hentAktørId(eq(søkerPersoninfo.getPersonIdent().getIdent()))).thenReturn(søker);
        when(oppslagTjeneste.hentPersoninfoFor(eq(søker))).thenReturn(søkerPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(søker))).thenReturn(søkerPersonhistorikk);

        when(oppslagTjeneste.hentAktørId(eq(annenPartPersoninfo.getPersonIdent().getIdent()))).thenReturn(annenPart);
        when(oppslagTjeneste.hentPersoninfoFor(eq(annenPart))).thenReturn(annenPartPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(annenPart))).thenReturn(annenPartPersonhistorikk);

        when(oppslagTjeneste.hentAktørId(eq(tpsFakta.getBarna().get(0).getPersoninfo().getPersonIdent().getIdent()))).thenReturn(barn);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barn))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barn))).thenReturn(barnPersonhistorikk);
    }

    @Test
    public void skal_lagre_ned_respons() {
        final var fagsak = Fagsak.opprettNy(søker, "123412341234");
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.save(behandling);

        tjeneste.innhentPersonopplysninger(behandling, FaktagrunnlagBuilder.hentSøknad());

        final var personopplysningGrunnlag = personopplysningService.hentHvisEksisterer(behandling);

        assert personopplysningGrunnlag.isPresent();
        final var registerVersjonOpt = personopplysningGrunnlag.get().getRegisterVersjon();
        assertThat(registerVersjonOpt).isPresent();

        final var informasjon = registerVersjonOpt.get();

        assertThat(informasjon.getAdresser()).hasSize(3);
        assertThat(informasjon.getPersonopplysninger()).hasSize(3);
        assertThat(informasjon.getRelasjoner()).hasSize(6);
        assertThat(informasjon.getStatsborgerskap()).hasSize(3);
    }
}
