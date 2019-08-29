package no.nav.familie.ks.sak.app.behandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.Ressurs;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.*;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository;
import no.nav.familie.ks.sak.app.grunnlag.OppslagTjeneste;
import no.nav.familie.ks.sak.app.rest.Behandling.*;
import no.nav.familie.ks.sak.util.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class BehandlingslagerService {

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
        final var søkerAktørId = oppslagTjeneste.hentAktørId(søknad.person.fnr);
        final var fagsak = Fagsak.opprettNy(søkerAktørId, Long.toString(System.currentTimeMillis())); // TODO: Erstatt med gsaksnummer
        fagsakRepository.saveAndFlush(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.saveAndFlush(behandling);

        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(Set.of(mapSøknadBarn(søknad).build()));
        familieforholdBuilder.setBorBeggeForeldreSammen(konverterTilBoolean(søknad.familieforhold.borForeldreneSammenMedBarnet));
        barnehageBarnGrunnlagRepository.save(new BarnehageBarnGrunnlag(behandling, familieforholdBuilder.build()));

        final var kravTilSoker = søknad.kravTilSoker;
        final var erklæring = new OppgittErklæring(konverterTilBoolean(kravTilSoker.barnIkkeHjemme),
                konverterTilBoolean(kravTilSoker.borSammenMedBarnet),
                konverterTilBoolean(kravTilSoker.ikkeAvtaltDeltBosted),
                konverterTilBoolean(kravTilSoker.skalBoMedBarnetINorgeNesteTolvMaaneder));

        final var oppgittUtlandsTilknytning = mapUtenlandsTilknytning(søknad, søkerAktørId);

        final var innsendtTidspunkt = LocalDateTime.ofInstant(søknad.innsendingsTidspunkt, ZoneId.systemDefault());
        søknadGrunnlagRepository.save(new SøknadGrunnlag(behandling, new Søknad(innsendtTidspunkt, oppgittUtlandsTilknytning, erklæring)));

        return behandling;
    }

    private OppgittUtlandsTilknytning mapUtenlandsTilknytning(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad, String søkerAktørId) {
        final var tilknytningTilUtland = søknad.tilknytningTilUtland;
        final var arbeidIUtlandet = søknad.arbeidIUtlandet;
        final var utenlandskeYtelser = søknad.utenlandskeYtelser;
        final var utenlandskKontantstotte = søknad.utenlandskKontantstotte;

        final var tilknytningUtlandSet = new HashSet<AktørTilknytningUtland>();
        final var arbeidYtelseUtlandSet = new HashSet<AktørArbeidYtelseUtland>();

        tilknytningUtlandSet.add(new AktørTilknytningUtland(søkerAktørId, tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar, tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAarForklaring));
        arbeidYtelseUtlandSet.add(new AktørArbeidYtelseUtland.Builder()
                .setAktørId(søkerAktørId)
                .setArbeidIUtlandet(Standpunkt.map(arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkel, Standpunkt.UBESVART))
                .setArbeidIUtlandetForklaring(arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkelForklaring)
                .setYtelseIUtlandet(Standpunkt.map(utenlandskeYtelser.mottarYtelserFraUtland, Standpunkt.UBESVART))
                .setYtelseIUtlandetForklaring(utenlandskeYtelser.mottarYtelserFraUtlandForklaring)
                .setKontantstøtteIUtlandet(Standpunkt.map(utenlandskKontantstotte.mottarKontantstotteFraUtlandet, Standpunkt.UBESVART))
                .setKontantstøtteIUtlandetForklaring(utenlandskKontantstotte.mottarKontantstotteFraUtlandetTilleggsinfo)
                .build());


        if (søknad.familieforhold.annenForelderFødselsnummer != null && !søknad.familieforhold.annenForelderFødselsnummer.isEmpty()) {
            final var annenPartAktørId = oppslagTjeneste.hentAktørId(søknad.familieforhold.annenForelderFødselsnummer);
            tilknytningUtlandSet.add(new AktørTilknytningUtland(annenPartAktørId, tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAar, tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAarForklaring));
            arbeidYtelseUtlandSet.add(new AktørArbeidYtelseUtland.Builder()
                    .setAktørId(annenPartAktørId)
                    .setArbeidIUtlandet(Standpunkt.map(arbeidIUtlandet.arbeiderAnnenForelderIUtlandet, Standpunkt.UBESVART))
                    .setArbeidIUtlandetForklaring(arbeidIUtlandet.arbeiderAnnenForelderIUtlandetForklaring)
                    .setYtelseIUtlandet(Standpunkt.map(utenlandskeYtelser.mottarAnnenForelderYtelserFraUtland, Standpunkt.UBESVART))
                    .setYtelseIUtlandetForklaring(utenlandskeYtelser.mottarAnnenForelderYtelserFraUtlandForklaring)
                    .build());
        }

        return new OppgittUtlandsTilknytning(arbeidYtelseUtlandSet, tilknytningUtlandSet);
    }

    private boolean konverterTilBoolean(String kode) {
        return Standpunkt.map(kode, Standpunkt.UBESVART).equals(Standpunkt.JA);
    }

    private Barn.Builder mapSøknadBarn(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
        final var builder = new Barn.Builder();
        final var mineBarn = søknad.getMineBarn();
        final var barnehageplass = søknad.barnehageplass;
        builder.setAktørId(mineBarn.getFødselsnummer())
                .setBarnehageStatus(BarnehageplassStatus.map(barnehageplass.barnBarnehageplassStatus.name()));
        switch (barnehageplass.barnBarnehageplassStatus) {
            case harBarnehageplass:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.harBarnehageplassAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.harBarnehageplassDato))
                        .setBarnehageKommune(barnehageplass.harBarnehageplassKommune);
                break;
            case harSluttetIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.harSluttetIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.harSluttetIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.harSluttetIBarnehageKommune);
                break;
            case skalSlutteIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.skalSlutteIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.skalSlutteIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.skalSlutteIBarnehageKommune);
                break;
            case skalBegynneIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.skalBegynneIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.skalBegynneIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.skalBegynneIBarnehageKommune);
                break;
        }

        return builder;
    }

    public Ressurs hentFagsakForSaksbehandler(Long fagsakId) {
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
            BehandlingResultat behandlingsresultat = behandlingresultatRepository.finnBehandlingsresultat(behandling.getId());
            Set<RestVilkårsresultat> restVilkårsResultat = new HashSet<>();
            behandlingsresultat.getVilkårsResultat().getVilkårsResultat().forEach(vilkårResultat ->
                    restVilkårsResultat.add(
                            new RestVilkårsresultat(
                                    vilkårResultat.getVilkårType(),
                                    vilkårResultat.getUtfall())));

            RestBehandlingsresultat restBehandlingsresultat = new RestBehandlingsresultat(restVilkårsResultat, behandlingsresultat.isAktiv());

            restBehandlinger.add(new RestBehandling(behandling.getId(), søknad, restBehandlingsresultat));
        });


        if (fagsak.isPresent()) {
            RestFagsak restFagsak = new RestFagsak(fagsak.get(), restBehandlinger);

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
