package no.nav.familie.ks.sak.app.behandling;

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
import no.nav.familie.ks.sak.app.grunnlag.OppslagTjeneste;
import no.nav.familie.ks.sak.util.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
public class BehandlingslagerTjeneste {

    private FagsakRepository fagsakRepository;
    private BehandlingRepository behandlingRepository;
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;
    private OppslagTjeneste oppslagTjeneste;

    @Autowired
    public BehandlingslagerTjeneste(FagsakRepository fagsakRepository,
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

    public void trekkUtOgPersister(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
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

}
