package no.nav.familie.ks.sak;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FunksjonelleMetrikker {
    private final HashMap<String, Counter> søkerUtfall = Maps.newHashMap();
    private final HashMap<String, Counter> vilkårIkkeOppfylt = Maps.newHashMap();

    private final HashMap<String, Counter> barnehagestatus = Maps.newHashMap();
    private final HashMap<String, Counter> boddEllerJobbetINorgeEllerEøsIFemÅr = Maps.newHashMap();
    private final Counter mottarKontantstotteFraUtlandet = Metrics.counter("soknad.kontantstotte.funksjonell.mottarKontantstotteFraUtlandet", "status", "JA");


    public FunksjonelleMetrikker() {
        Lists.newArrayList(UtfallType.values()).forEach(utfallType -> søkerUtfall.put(
                utfallType.name(),
                Metrics.counter("soknad.kontantstotte.behandling.funksjonell.utfall", "status", utfallType.name(), "beskrivelse", utfallType.getBeskrivelse())
        ));

        Lists.newArrayList(VilkårIkkeOppfyltÅrsak.values()).forEach(vilkårIkkeOppfyltÅrsak -> vilkårIkkeOppfylt.put(
                Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()),
                Metrics.counter("soknad.kontantstotte.behandling.funksjonell.avslag", "status", Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()), "beskrivelse", vilkårIkkeOppfyltÅrsak.getBeskrivelse())
        ));

        Lists.newArrayList(Barnehageplass.BarnehageplassVerdier.values()).forEach(barnehageplassVerdi -> barnehagestatus.put(
                barnehageplassVerdi.name(),
                Metrics.counter("soknad.kontantstotte.funksjonell.barnehage", "status", barnehageplassVerdi.name(), "beskrivelse", barnehageplassVerdi.getBeskrivelse())
        ));

        Lists.newArrayList(TilknytningTilUtland.TilknytningTilUtlandVerdier.values()).forEach(tilknytningTilUtlandVerdi -> boddEllerJobbetINorgeEllerEøsIFemÅr.put(
                tilknytningTilUtlandVerdi.name(),
                Metrics.counter("soknad.kontantstotte.funksjonell.boddEllerJobbetINorgeEllerEøsIFemÅr", "status", tilknytningTilUtlandVerdi.name(), "beskrivelse", tilknytningTilUtlandVerdi.getBeskrivelse())
        ));
    }

    public void tellFunksjonelleMetrikker(Søknad søknad, Vedtak vedtak) {
        søkerUtfall.get(vedtak.getVilkårvurdering().getUtfallType().name()).increment();
        if (vedtak.getVilkårvurdering().getUtfallType().equals(UtfallType.IKKE_OPPFYLT)) {
            vilkårIkkeOppfylt.get(Integer.toString(vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode())).increment();
        }

        barnehagestatus.get(søknad.barnehageplass.barnBarnehageplassStatus.name()).increment();
        boddEllerJobbetINorgeEllerEøsIFemÅr.get(søknad.tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar.name()).increment();

        if (søknad.utenlandskKontantstotte.mottarKontantstotteFraUtlandet.equals("JA")) {
            mottarKontantstotteFraUtlandet.increment();
        }
    }
}
