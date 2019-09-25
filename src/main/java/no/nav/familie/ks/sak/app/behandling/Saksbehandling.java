package no.nav.familie.ks.sak.app.behandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

@Service
public class Saksbehandling {
    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private BehandlingslagerService behandlingslagerService;
    private RegisterInnhentingService registerInnhentingService;
    private FastsettingService fastsettingService;
    private ResultatService resultatService;
    private ObjectMapper mapper;

    @Autowired
    public Saksbehandling(VurderSamletTjeneste vurderSamletTjeneste,
                          BehandlingslagerService behandlingslagerService,
                          RegisterInnhentingService registerInnhentingService,
                          FastsettingService fastsettingService,
                          ResultatService resultatService,
                          ObjectMapper objectMapper) {
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.behandlingslagerService = behandlingslagerService;
        this.registerInnhentingService = registerInnhentingService;
        this.fastsettingService = fastsettingService;
        this.resultatService = resultatService;
        this.mapper = objectMapper;
    }

    public Vedtak behandle(String søknadJson) {
        return behandle(tilSøknad(søknadJson));
    }

    @Transactional
    @Retryable(
        value = { Exception.class },
        maxAttempts = 2,
        backoff = @Backoff(delay = 5000))
    public Vedtak behandle(Søknad søknad) {
        final Behandling behandling = behandlingslagerService.nyBehandling(søknad);
        TpsFakta tpsFakta = registerInnhentingService.innhentPersonopplysninger(behandling, søknad);
        behandlingslagerService.trekkUtOgPersister(behandling, søknad);
        Faktagrunnlag faktagrunnlag = fastsettingService.fastsettFakta(behandling, tpsFakta);

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

    private Søknad tilSøknad(String json) {
        try {
            return mapper.readValue(new File(json), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}