package no.nav.familie.ks.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.familie.ks.sak.behandling.VilkårRegel;
import no.nav.familie.ks.sak.behandling.Regelresultat;
import no.nav.familie.ks.sak.behandling.grunnlag.Vilkårvurdering;
import no.nav.familie.ks.sak.behandling.grunnlag.Faktagrunnlag;
import no.nav.familie.ks.sak.felles.GradertPeriode;
import no.nav.familie.ks.sak.resultat.UtfallType;
import no.nav.familie.ks.sak.resultat.Vedtak;
import no.nav.familie.ks.sak.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.grunnlag.Søknad;
import no.nav.familie.ks.sak.behandling.Oppslag;
import no.nav.familie.ks.sak.behandling.PeriodeOppretter;
import no.nav.familie.ks.sak.behandling.VilkårRegelFeil;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;


public class Saksbehandling {

    private final JacksonJsonConfig jacksonJsonConfig = new JacksonJsonConfig();

    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private Oppslag oppslag = new Oppslag();
    private VilkårRegel vilkår = new VilkårRegel();

    public Vedtak behandle(Søknad søknad) {
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad);
        Vilkårvurdering vilkårvurdering = vurderVilkår(vilkår, faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    private Faktagrunnlag fastsettFakta(Søknad søknad) {
        // søknadsdata, TPS-data og evt. barnehagelister
        TpsFakta tpsFakta = oppslag.hentTpsFakta("123456789");
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFakta)
                .medSøknad(søknad)
                .build();
    }

    private Vilkårvurdering vurderVilkår(VilkårRegel vilkår, Faktagrunnlag grunnlag) {
        var evaluering = vilkår.evaluer(grunnlag);
        var inputJson = toJson(grunnlag);
        var regelJson = EvaluationSerializer.asJson(evaluering);
        var regelresultat = new Regelresultat(evaluering);
        var utfallType = regelresultat.getUtfallType();
        var årsak = regelresultat.getUtfallÅrsak();
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
            return jacksonJsonConfig.toJson(grunnlag);
        } catch (JsonProcessingException e) {
            throw new VilkårRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
        }
    }

    public void setOppslag(Oppslag oppslag) {
        this.oppslag = oppslag;
    }
}
