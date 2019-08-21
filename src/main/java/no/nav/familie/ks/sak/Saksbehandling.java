package no.nav.familie.ks.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.*;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.OppslagTjeneste;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

@Service
public class Saksbehandling {

    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private BehandlingslagerTjeneste behandlingslagerTjeneste;
    private ObjectMapper mapper;
    private OppslagTjeneste oppslag;

    @Autowired
    public Saksbehandling(OppslagTjeneste oppslag,
                          VurderSamletTjeneste vurderSamletTjeneste,
                          BehandlingslagerTjeneste behandlingslagerTjeneste,
                          ObjectMapper objectMapper) {
        this.oppslag = oppslag;
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.behandlingslagerTjeneste = behandlingslagerTjeneste;
        this.mapper = objectMapper;
    }

    public Vedtak behandle(String søknadJson, String personident) {
        Søknad søknad = tilSøknad(søknadJson);
        behandlingslagerTjeneste.trekkUtOgPersister(søknad);
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    public Vedtak behandle(Søknad søknad, String personident) {
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    private Faktagrunnlag fastsettFakta(Søknad søknad, String personident) {
        // søknadsdata, TPS-data og evt. barnehagelister
        TpsFakta tpsFakta = oppslag.hentTpsFakta(søknad, personident);
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFakta)
                .medSøknad(søknad)
                .build();
    }

    private SamletVilkårsVurdering vurderVilkår(Faktagrunnlag grunnlag) {
        return vurderSamletTjeneste.vurder(grunnlag);
    }

    private GradertPeriode fastsettPeriode(Faktagrunnlag grunnlag) {
        return periodeOppretter.opprettStønadPeriode(grunnlag);
    }

    private Vedtak fattVedtak(SamletVilkårsVurdering vilkårvurdering, Faktagrunnlag faktagrunnlag) {
        UtfallType utfallType = vilkårvurdering.getUtfallType();
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
