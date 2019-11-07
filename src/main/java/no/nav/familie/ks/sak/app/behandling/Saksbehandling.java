package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.avvik.AvviksVurdering;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.MedlFakta;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import no.nav.familie.ks.sak.app.integrasjon.oppgave.domene.OppgaveBeskrivelse;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.FDATException;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.InfotrygdFakta;

import no.nav.familie.ks.sak.config.toggle.UnleashProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Saksbehandling {

    private static final Logger LOG = LoggerFactory.getLogger(Saksbehandling.class);
    private static final String OPPDATER_OPPGAVE = "familie-ks-sak.oppdater_oppgave";

    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private BehandlingslagerService behandlingslagerService;
    private RegisterInnhentingService registerInnhentingService;
    private FastsettingService fastsettingService;
    private ResultatService resultatService;
    private OppslagTjeneste oppslagTjeneste;
    private UnleashProvider unleash;

    @Autowired
    public Saksbehandling(VurderSamletTjeneste vurderSamletTjeneste,
                          BehandlingslagerService behandlingslagerService,
                          RegisterInnhentingService registerInnhentingService,
                          FastsettingService fastsettingService,
                          OppslagTjeneste oppslagTjeneste,
                          UnleashProvider unleash,
                          ResultatService resultatService) {
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.behandlingslagerService = behandlingslagerService;
        this.registerInnhentingService = registerInnhentingService;
        this.fastsettingService = fastsettingService;
        this.resultatService = resultatService;
        this.oppslagTjeneste = oppslagTjeneste;
        this.unleash = unleash;
    }

    @Transactional
    public Vedtak behandle(Søknad søknad, String saksnummer, String journalpostID) {
        final Behandling behandling = behandlingslagerService.nyBehandling(søknad, saksnummer, journalpostID);
        behandlingslagerService.trekkUtOgPersisterSøknad(behandling, søknad);

        TpsFakta tpsFakta;
        try {
            tpsFakta = registerInnhentingService.innhentPersonopplysninger(behandling, søknad);
        } catch (FDATException e) {
            Vedtak vedtak = new Vedtak(new AvviksVurdering());
            vedtak.setBehandlingsId(behandling.getId());
            resultatService.persisterResultat(behandling, vedtak.getVilkårvurdering());
            vedtak.setBehandlingsId(behandling.getId());
            vedtak.setFagsakId(behandling.getFagsak().getId());
            oppdaterGosysOppgave(vedtak, søknad, journalpostID);
            return vedtak;
        }
        MedlFakta medlFakta = registerInnhentingService.hentMedlemskapsopplysninger(behandling);
        InfotrygdFakta infotrygdFakta = registerInnhentingService.hentInfotrygdFakta(søknad);
        Faktagrunnlag faktagrunnlag = fastsettingService.fastsettFakta(behandling, tpsFakta, medlFakta, infotrygdFakta);

        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(behandling, faktagrunnlag);

        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        vedtak.setBehandlingsId(behandling.getId());
        vedtak.setFagsakId(behandling.getFagsak().getId());
        oppdaterGosysOppgave(vedtak, søknad, journalpostID);
        return vedtak;
    }

    private void oppdaterGosysOppgave(Vedtak vedtak, Søknad søknad, String journalpostID) {
        var vilkårvurdering = vedtak.getVilkårvurdering();
        var samletUtfallType = vilkårvurdering.getSamletUtfallType();
        String oppgaveBeskrivelse;

        if (vilkårvurdering instanceof SamletVilkårsVurdering) {
            var samletVilkårVurdering = (SamletVilkårsVurdering) vilkårvurdering;
            if (samletUtfallType.equals(UtfallType.OPPFYLT)) {
                LOG.info("Søknad kan behandles automatisk. Fagsak-ID: {}, Årsak: {}", vedtak.getFagsakId(), samletUtfallType);
                oppgaveBeskrivelse = String.format(OppgaveBeskrivelse.FORESLÅ_VEDTAK, OppgaveBeskrivelse.args(vedtak, søknad));
            } else {
                LOG.info("Søknad kan ikke behandles automatisk. Fagsak-ID: {}, Årsak: {}", vedtak.getFagsakId(), samletVilkårVurdering.getResultater());
                oppgaveBeskrivelse = OppgaveBeskrivelse.MANUELL_BEHANDLING;
            }
        } else {
            var avviksvurdering = (AvviksVurdering) vilkårvurdering;
            LOG.info("Søknad ble avvikshåndtert. Fagsak-ID: {}, Årsak: {}", vedtak.getFagsakId(), avviksvurdering.getAvvik().keySet());
            oppgaveBeskrivelse = OppgaveBeskrivelse.MANUELL_BEHANDLING;
        }

        if (unleash.toggle(OPPDATER_OPPGAVE).isEnabled()) {
            LOG.info("Oppdater oppgave toggle er: Enabled\n Kaller oppslagTjeneste.oppdaterGosysOppgave...");
            oppslagTjeneste.oppdaterGosysOppgave(søknad.getSøkerFødselsnummer(), journalpostID, oppgaveBeskrivelse);
        } else {
            LOG.info("Oppdater oppgave toggle er: Disabled");
        }
    }

    private SamletVilkårsVurdering vurderVilkår(Behandling behandling, Faktagrunnlag grunnlag) {
        final var samletVilkårsVurdering = vurderSamletTjeneste.vurder(grunnlag);

        resultatService.persisterResultat(behandling, samletVilkårsVurdering);
        return samletVilkårsVurdering;
    }

    private GradertPeriode fastsettPeriode(Faktagrunnlag grunnlag) {
        return periodeOppretter.opprettStønadPeriode(grunnlag);
    }

    private Vedtak fattVedtak(SamletVilkårsVurdering vilkårvurdering, Faktagrunnlag faktagrunnlag) {
        UtfallType utfallType = vilkårvurdering.getSamletUtfallType();
        switch (utfallType) {
            case MANUELL_BEHANDLING:
                return new Vedtak(vilkårvurdering);
            case OPPFYLT:
                GradertPeriode stønadperiode = fastsettPeriode(faktagrunnlag);
                return new Vedtak(vilkårvurdering, stønadperiode);
            default:
                throw new UnsupportedOperationException(String.format("Ukjent utfalltype: %s", utfallType.name()));
        }
    }
}
