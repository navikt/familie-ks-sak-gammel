package no.nav.familie.ks.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.*;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Vilkårvurdering;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårÅrsak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

@Component
public class Saksbehandling {

    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private ObjectMapper mapper;
    private Oppslag oppslag;

    @Autowired
    public Saksbehandling(Oppslag oppslag, ObjectMapper objectMapper) {
        this.oppslag = oppslag;
        this.mapper = objectMapper;
    }

    public Vedtak behandle(String søknadJson, String personident) {
        Søknad søknad = tilSøknad(søknadJson);
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        Vilkårvurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    public Vedtak behandle(Søknad søknad, String personident) {
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        Vilkårvurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
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

    private Vilkårvurdering vurderVilkår(Faktagrunnlag grunnlag) {
        var vilkår = new AutomatiskBehandleKontantstøtteRegel();
        Evaluation evaluering = vilkår.evaluer(grunnlag);
        String inputJson = toJson(grunnlag);
        String regelJson = EvaluationSerializer.asJson(evaluering);
        Regelresultat regelresultat = new Regelresultat(evaluering);
        UtfallType utfallType = regelresultat.getUtfallType();
        VilkårÅrsak årsak = regelresultat.getUtfallÅrsak();
        return new Vilkårvurdering.Builder()
                .medInputJson(inputJson)
                .medRegelJson(regelJson)
                .medVilkårÅrsak(årsak)
                .medUtfallType(utfallType)
                .build();
    }

    private GradertPeriode fastsettPeriode(Faktagrunnlag grunnlag) {
        return periodeOppretter.opprettStønadPeriode(grunnlag);
    }

    private Vedtak fattVedtak(Vilkårvurdering vilkårvurdering, Faktagrunnlag faktagrunnlag) {
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
