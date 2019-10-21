package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.MedlFakta;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.FDATException;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.InfotrygdFakta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Saksbehandling {
    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private BehandlingslagerService behandlingslagerService;
    private RegisterInnhentingService registerInnhentingService;
    private FastsettingService fastsettingService;
    private ResultatService resultatService;

    @Autowired
    public Saksbehandling(VurderSamletTjeneste vurderSamletTjeneste,
                          BehandlingslagerService behandlingslagerService,
                          RegisterInnhentingService registerInnhentingService,
                          FastsettingService fastsettingService,
                          ResultatService resultatService) {
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.behandlingslagerService = behandlingslagerService;
        this.registerInnhentingService = registerInnhentingService;
        this.fastsettingService = fastsettingService;
        this.resultatService = resultatService;
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
            return vedtak;
        }
        MedlFakta medlFakta = registerInnhentingService.hentMedlemskapsopplysninger(behandling);
        InfotrygdFakta infotrygdFakta = registerInnhentingService.hentInfotrygdFakta(søknad);
        Faktagrunnlag faktagrunnlag = fastsettingService.fastsettFakta(behandling, tpsFakta, medlFakta, infotrygdFakta);

        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(behandling, faktagrunnlag);

        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        vedtak.setBehandlingsId(behandling.getId());
        return vedtak;
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
