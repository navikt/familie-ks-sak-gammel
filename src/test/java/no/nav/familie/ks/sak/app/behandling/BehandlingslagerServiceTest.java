package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.grunnlag.OppslagTjeneste;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {ApplicationConfig.class},
        loader = AnnotationConfigContextLoader.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
public class BehandlingslagerServiceTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;
    @MockBean
    private StsRestClient stsRestClient;

    @Autowired
    private BehandlingslagerService tjeneste;

    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private BehandlingRepository behandlingRepository;
    @Autowired
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    @Autowired
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;

    @Before
    public void setUp() throws Exception {
        Mockito.when(oppslagTjeneste.hentAktørId(ArgumentMatchers.any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void skal_lagre_søknad_og_hente_opp_igjen() {
        final var søknad = FaktagrunnlagBuilder.søknadMedBarnehageplass();
        tjeneste.trekkUtOgPersister(søknad);

        final var fagsaker = fagsakRepository.findAll();
        assertThat(fagsaker).hasSize(1);
        assertThat(fagsaker.get(0).getAktørId()).isEqualTo(søknad.person.fnr);
        final var behandlinger = behandlingRepository.findAll();
        assertThat(behandlinger).hasSize(1);
        final var behandling = behandlinger.get(0);
        final var søknader = søknadGrunnlagRepository.findAll();
        final var søknad1 = søknader.get(0).getSøknad();
        assertThat(søknad1.getInnsendtTidspunkt()).isEqualTo(LocalDateTime.ofInstant(søknad.innsendingsTidspunkt, ZoneId.systemDefault()));
        final var erklæring = søknad1.getErklæring();
        assertThat(erklæring).isNotNull();
        assertThat(søknad1.getUtlandsTilknytning()).isNotNull();
        assertThat(søknad1.getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet()).hasSize(1);
        assertThat(søknad1.getUtlandsTilknytning().getAktørerTilknytningTilUtlandet()).hasSize(1);
        final var barnehageBarnGrunnlagList = barnehageBarnGrunnlagRepository.findAll();
        assertThat(barnehageBarnGrunnlagList).hasSize(1);
        assertThat(barnehageBarnGrunnlagList.get(0).getFamilieforhold()).isNotNull();
    }
}