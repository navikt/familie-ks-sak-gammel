package no.nav.familie.ks.sak.app.behandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.SøknadTilGrunnlagMapper;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittErklæring;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.rest.Behandling.*;
import no.nav.familie.ks.sak.util.Ressurs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static no.nav.familie.ks.sak.util.Konvertering.konverterTilBoolean;

@Service
public class BehandlingslagerService {

    private static final Logger logger = LoggerFactory.getLogger(BehandlingslagerService.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private FagsakRepository fagsakRepository;
    private BehandlingRepository behandlingRepository;
    private BehandlingresultatRepository behandlingresultatRepository;
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;
    private OppslagTjeneste oppslagTjeneste;
    private ObjectMapper objectMapper;

    @Autowired
    public BehandlingslagerService(FagsakRepository fagsakRepository,
                                   BehandlingRepository behandlingRepository,
                                   BehandlingresultatRepository behandlingresultatRepository,
                                   SøknadGrunnlagRepository søknadGrunnlagRepository,
                                   BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository,
                                   OppslagTjeneste oppslag,
                                   ObjectMapper objectMapper) {
        this.fagsakRepository = fagsakRepository;
        this.behandlingRepository = behandlingRepository;
        this.behandlingresultatRepository = behandlingresultatRepository;
        this.søknadGrunnlagRepository = søknadGrunnlagRepository;
        this.barnehageBarnGrunnlagRepository = barnehageBarnGrunnlagRepository;
        this.oppslagTjeneste = oppslag;
        this.objectMapper = objectMapper;
    }

    public Behandling trekkUtOgPersister(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
        final var søkerAktørId = oppslagTjeneste.hentAktørId(søknad.getPerson().getFnr());
        final var fagsak = Fagsak.opprettNy(søkerAktørId, Long.toString(System.currentTimeMillis())); // TODO: Erstatt med gsaksnummer
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.save(behandling);

        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(Set.of(SøknadTilGrunnlagMapper.mapSøknadBarn(søknad).build()));
        familieforholdBuilder.setBorBeggeForeldreSammen(konverterTilBoolean(søknad.getFamilieforhold().getBorForeldreneSammenMedBarnet()));
        barnehageBarnGrunnlagRepository.save(new BarnehageBarnGrunnlag(behandling, familieforholdBuilder.build()));

        final var kravTilSoker = søknad.kravTilSoker;
        final var erklæring = new OppgittErklæring(konverterTilBoolean(kravTilSoker.barnIkkeHjemme),
            konverterTilBoolean(kravTilSoker.borSammenMedBarnet),
            konverterTilBoolean(kravTilSoker.ikkeAvtaltDeltBosted),
            konverterTilBoolean(kravTilSoker.skalBoMedBarnetINorgeNesteTolvMaaneder));

        AktørId oppgittAnnenPartAktørId = null;
        if (søknad.getFamilieforhold().getAnnenForelderFødselsnummer() != null && !søknad.getFamilieforhold().getAnnenForelderFødselsnummer().isEmpty()) {
            try {
                oppgittAnnenPartAktørId = oppslagTjeneste.hentAktørId(søknad.getFamilieforhold().getAnnenForelderFødselsnummer());
            } catch (Exception e) {
                logger.warn("Oppslag på aktørid på oppgitt fnr på annen part feilet.");
                secureLogger.info("Oppslag på aktørid på oppgitt fnr: {}, på annen part feilet.", søknad.getFamilieforhold().getAnnenForelderFødselsnummer());
            }
        }
        final var oppgittUtlandsTilknytning = SøknadTilGrunnlagMapper.mapUtenlandsTilknytning(søknad, søkerAktørId, oppgittAnnenPartAktørId);

        final var innsendtTidspunkt = LocalDateTime.ofInstant(søknad.innsendingsTidspunkt, ZoneId.systemDefault());
        søknadGrunnlagRepository.save(new SøknadGrunnlag(behandling, new Søknad(innsendtTidspunkt, oppgittUtlandsTilknytning, erklæring)));

        return behandling;
    }

    RestFagsak hentRestFagsak(Long fagsakId) {
        Optional<Fagsak> fagsak = fagsakRepository.findById(fagsakId);
        List<Behandling> behandlinger = behandlingRepository.finnBehandlinger(fagsakId);

        // Grunnlag fra søknag
        List<RestBehandling> restBehandlinger = new ArrayList<>();
        behandlinger.forEach(behandling -> {
            SøknadGrunnlag søknadGrunnlag = søknadGrunnlagRepository.finnGrunnlag(behandling.getId());
            BarnehageBarnGrunnlag barnehageBarnGrunnlag = barnehageBarnGrunnlagRepository.finnGrunnlag(behandling.getId());

            Set<RestBarn> barna = new HashSet<>();
            barnehageBarnGrunnlag.getFamilieforhold().getBarna().forEach(barn ->
                    barna.add(
                            new RestBarn(
                            barn.getAktørId(),
                            barn.getBarnehageStatus(),
                            barn.getBarnehageAntallTimer(),
                            barn.getBarnehageDato(),
                            barn.getBarnehageKommune())));
            RestOppgittFamilieforhold familieforhold = new RestOppgittFamilieforhold(barna, barnehageBarnGrunnlag.getFamilieforhold().isBorBeggeForeldreSammen());

            Set<RestAktørArbeidYtelseUtland> aktørerArbeidYtelseUtland = new HashSet<>();
            Set<RestAktørTilknytningUtland> aktørerTilknytningUtland = new HashSet<>();
            søknadGrunnlag.getSøknad().getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet().forEach(aktørArbeidYtelseUtland ->
                    aktørerArbeidYtelseUtland.add(
                            new RestAktørArbeidYtelseUtland(
                                    aktørArbeidYtelseUtland.getAktørId(),
                                    aktørArbeidYtelseUtland.getArbeidIUtlandet(),
                                    aktørArbeidYtelseUtland.getArbeidIUtlandetForklaring(),
                                    aktørArbeidYtelseUtland.getYtelseIUtlandet(),
                                    aktørArbeidYtelseUtland.getYtelseIUtlandetForklaring(),
                                    aktørArbeidYtelseUtland.getKontantstøtteIUtlandet(),
                                    aktørArbeidYtelseUtland.getKontantstøtteIUtlandetForklaring())));
            søknadGrunnlag.getSøknad().getUtlandsTilknytning().getAktørerTilknytningTilUtlandet().forEach(aktørTilknytningUtland ->
                    aktørerTilknytningUtland.add(
                            new RestAktørTilknytningUtland(
                                    aktørTilknytningUtland.getAktør(),
                                    aktørTilknytningUtland.getTilknytningTilUtland(),
                                    aktørTilknytningUtland.getTilknytningTilUtlandForklaring())));

            RestOppgittUtlandsTilknytning oppgittUtlandsTilknytning = new RestOppgittUtlandsTilknytning(aktørerArbeidYtelseUtland, aktørerTilknytningUtland);

            OppgittErklæring erklæring = søknadGrunnlag.getSøknad().getErklæring();
            RestOppgittErklæring oppgittErklæring = new RestOppgittErklæring(erklæring.isBarnetHjemmeværendeOgIkkeAdoptert(), erklæring.isBorSammenMedBarnet(), erklæring.isIkkeAvtaltDeltBosted(), erklæring. isBarnINorgeNeste12Måneder());

            RestSøknad søknad = new RestSøknad(søknadGrunnlag.getSøknad().getInnsendtTidspunkt(), familieforhold, oppgittUtlandsTilknytning, oppgittErklæring);


            // Grunnlag fra regelkjøring
            BehandlingResultat behandlingResultat = behandlingresultatRepository.finnBehandlingsresultat(behandling.getId());
            Set<RestVilkårsResultat> restVilkårsResultat = new HashSet<>();
            behandlingResultat.getVilkårsResultat().getVilkårsResultat().forEach(vilkårResultat ->
                    restVilkårsResultat.add(
                            new RestVilkårsResultat(
                                    vilkårResultat.getVilkårType(),
                                    vilkårResultat.getUtfall())));

            RestBehandlingsresultat restBehandlingsresultat = new RestBehandlingsresultat(restVilkårsResultat, behandlingResultat.isAktiv());

            restBehandlinger.add(new RestBehandling(behandling.getId(), søknad, restBehandlingsresultat));
        });

        return fagsak.map(fagsak1 -> new RestFagsak(fagsak1, restBehandlinger)).orElse(null);
    }

    public Ressurs hentRessursFagsak(Long fagsakId) {
        RestFagsak restFagsak = hentRestFagsak(fagsakId);

        if (restFagsak != null) {
            return new Ressurs.Builder().byggVellyketRessurs(objectMapper.valueToTree(restFagsak));
        } else {
            return new Ressurs.Builder()
                    .byggFeiletRessurs("Fant ikke fagsak med id " + fagsakId);
        }
    }

    public List<Fagsak> hentFagsaker() {
        return fagsakRepository.findAll();
    }

}
