package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.SøknadTilGrunnlagMapper;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittErklæring;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BehandlingslagerService {

    private static final Logger logger = LoggerFactory.getLogger(BehandlingslagerService.class);
    private FagsakRepository fagsakRepository;
    private BehandlingRepository behandlingRepository;
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;
    private PersonopplysningRepository personopplysningRepository;
    private OppslagTjeneste oppslagTjeneste;

    @Autowired
    public BehandlingslagerService(FagsakRepository fagsakRepository,
                                   BehandlingRepository behandlingRepository,
                                   SøknadGrunnlagRepository søknadGrunnlagRepository,
                                   BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository,
                                   PersonopplysningRepository personopplysningRepository,
                                   OppslagTjeneste oppslag) {
        this.fagsakRepository = fagsakRepository;
        this.behandlingRepository = behandlingRepository;
        this.søknadGrunnlagRepository = søknadGrunnlagRepository;
        this.barnehageBarnGrunnlagRepository = barnehageBarnGrunnlagRepository;
        this.personopplysningRepository = personopplysningRepository;
        this.oppslagTjeneste = oppslag;
    }

    public Behandling nyBehandling(Søknad søknad, String saksnummer) {
        final var søkerAktørId = oppslagTjeneste.hentAktørId(søknad.getSøkerFødselsnummer());
        final var fagsak = Fagsak.opprettNy(søkerAktørId, saksnummer);
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.save(behandling);

        return behandling;
    }

    void trekkUtOgPersister(Behandling behandling, Søknad søknad) {
        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(SøknadTilGrunnlagMapper.mapSøknadBarn(søknad));
        familieforholdBuilder.setBorBeggeForeldreSammen(søknad.getOppgittFamilieforhold().getBorBeggeForeldreSammen());
        barnehageBarnGrunnlagRepository.save(new BarnehageBarnGrunnlag(behandling, familieforholdBuilder.build()));

        final var kravTilSoker = søknad.getOppgittErklæring();
        final var erklæring = new OppgittErklæring(kravTilSoker.isBarnetHjemmeværendeOgIkkeAdoptert(),
            kravTilSoker.isBorSammenMedBarnet(),
            kravTilSoker.isIkkeAvtaltDeltBosted(),
            kravTilSoker.isBarnINorgeNeste12Måneder());

        final var oppgittAnnenPartFødselsnummer = søknad.getOppgittAnnenPartFødselsnummer();
        AktørId oppgittAnnenPartAktørId = null;

        if (oppgittAnnenPartFødselsnummer != null && !oppgittAnnenPartFødselsnummer.isEmpty()) {
            Optional<PersonopplysningGrunnlag> personopplysningGrunnlag = personopplysningRepository.findByBehandlingAndAktiv(behandling.getId());
            if (personopplysningGrunnlag.isPresent()) {
                Optional<AktørId> oppgittAnnenPart = personopplysningGrunnlag.get().getOppgittAnnenPart();
                if (oppgittAnnenPart.isPresent()) {
                    oppgittAnnenPartAktørId = oppgittAnnenPart.get();
                } else {
                    logger.warn("Annen part mangler i personopplysning grunnlaget, men søker har oppgitt annen part");
                }
            } else {
                logger.warn("Personopplysning grunnlaget mangler.");
            }
        }

        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(søknad, behandling.getFagsak().getAktørId(), oppgittAnnenPartAktørId);

        søknadGrunnlagRepository.save(new SøknadGrunnlag(behandling, new no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad(søknad.getInnsendtTidspunkt(), oppgittUtlandsTilknytning, erklæring)));
    }
}
