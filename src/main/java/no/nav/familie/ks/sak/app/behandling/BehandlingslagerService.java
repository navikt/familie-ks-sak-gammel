package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.SøknadTilGrunnlagMapper;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittErklæring;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BehandlingslagerService {

    private FagsakRepository fagsakRepository;
    private BehandlingRepository behandlingRepository;
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;
    private OppslagTjeneste oppslagTjeneste;

    @Autowired
    public BehandlingslagerService(FagsakRepository fagsakRepository,
                                   BehandlingRepository behandlingRepository,
                                   SøknadGrunnlagRepository søknadGrunnlagRepository,
                                   BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository,
                                   OppslagTjeneste oppslag) {
        this.fagsakRepository = fagsakRepository;
        this.behandlingRepository = behandlingRepository;
        this.søknadGrunnlagRepository = søknadGrunnlagRepository;
        this.barnehageBarnGrunnlagRepository = barnehageBarnGrunnlagRepository;
        this.oppslagTjeneste = oppslag;
    }

    public Behandling nyBehandling(Søknad søknad, String saksnummer, String journalpostID) {
        final var søkerAktørId = oppslagTjeneste.hentAktørId(søknad.getSøkerFødselsnummer());
        final var fagsak = Fagsak.opprettNy(søkerAktørId, saksnummer);
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak, journalpostID).build();
        behandlingRepository.save(behandling);

        return behandling;
    }

    void trekkUtOgPersister(Behandling behandling, Søknad søknad) {
        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(mapOgHentBarna(søknad));
        familieforholdBuilder.setBorBeggeForeldreSammen(søknad.getOppgittFamilieforhold().getBorBeggeForeldreSammen());
        barnehageBarnGrunnlagRepository.save(new BarnehageBarnGrunnlag(behandling, familieforholdBuilder.build()));

        final var kravTilSoker = søknad.getOppgittErklæring();
        final var erklæring = new OppgittErklæring(kravTilSoker.isBarnetHjemmeværendeOgIkkeAdoptert(),
            kravTilSoker.isBorSammenMedBarnet(),
            kravTilSoker.isIkkeAvtaltDeltBosted(),
            kravTilSoker.isBarnINorgeNeste12Måneder());

        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(søknad);

        søknadGrunnlagRepository.save(new SøknadGrunnlag(behandling,
            new no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad(søknad.getInnsendtTidspunkt(), søknad.getSøkerFødselsnummer(), søknad.getOppgittAnnenPartFødselsnummer(), oppgittUtlandsTilknytning, erklæring)));
    }

    private Set<Barn> mapOgHentBarna(Søknad søknad) {
        Set<Barn> barna = SøknadTilGrunnlagMapper.mapSøknadBarn(søknad);
        for (Barn barn : barna) {
            barn.setFødselsnummer(barn.getFødselsnummer());
        }
        return barna;
    }
}
