package no.nav.familie.ks.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.*;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

@Service
public class Saksbehandling {

    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private BehandlingslagerService behandlingslagerService;
    private RegisterInnhentingService registerInnhentingService;
    private ResultatService resultatService;
    private ObjectMapper mapper;
    private OppslagTjeneste oppslag;

    @Autowired
    public Saksbehandling(OppslagTjeneste oppslag,
                          VurderSamletTjeneste vurderSamletTjeneste,
                          BehandlingslagerService behandlingslagerService,
                          RegisterInnhentingService registerInnhentingService,
                          ResultatService resultatService,
                          ObjectMapper objectMapper) {
        this.oppslag = oppslag;
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.behandlingslagerService = behandlingslagerService;
        this.registerInnhentingService = registerInnhentingService;
        this.resultatService = resultatService;
        this.mapper = objectMapper;
    }

    public Vedtak behandle(String søknadJson) {
        Søknad søknad = tilSøknad(søknadJson);
        final var behandling = behandlingslagerService.trekkUtOgPersister(søknad);
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad);
        registerInnhentingService.innhentPersonopplysninger(behandling, søknad);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(behandling, faktagrunnlag);

        return fattVedtak(vilkårvurdering, faktagrunnlag);
    }

    public Vedtak behandle(Søknad søknad) {
        final var behandling = behandlingslagerService.trekkUtOgPersister(søknad);
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad);
        registerInnhentingService.innhentPersonopplysninger(behandling, søknad);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(behandling, faktagrunnlag);

        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        vedtak.setBehandlingsId(behandling.getId());
        return vedtak;
    }

    private Faktagrunnlag fastsettFakta(Søknad søknad) {
        // søknadsdata, TPS-data og evt. barnehagelister
        TpsFakta tpsFakta = oppslag.hentTpsFakta(søknad.getPerson().getFnr(), søknad.getFamilieforhold().getAnnenForelderFødselsnummer(), søknad.getMineBarn().getFødselsnummer());
        return new Faktagrunnlag.Builder()
            .medTpsFakta(tpsFakta)
            .medSøknad(søknad)
            .build();
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
            case IKKE_OPPFYLT:
                return new Vedtak(vilkårvurdering);
            case OPPFYLT:
                GradertPeriode stønadperiode = fastsettPeriode(faktagrunnlag);
                return new Vedtak(vilkårvurdering, stønadperiode);
            default:
                throw new UnsupportedOperationException(String.format("Ukjent utfalltype: %s", utfallType.name()));
        }
    }

    private String toJson(Faktagrunnlag grunnlag) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(grunnlag);
        } catch (JsonProcessingException e) {
            throw new VilkårRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
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
