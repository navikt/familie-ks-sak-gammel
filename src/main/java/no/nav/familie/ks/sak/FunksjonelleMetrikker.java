package no.nav.familie.ks.sak;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class FunksjonelleMetrikker {
    private static final Logger log = LoggerFactory.getLogger(FunksjonelleMetrikker.class);

    private final HashMap<String, Counter> søkerUtfall = new HashMap<>();
    private final HashMap<String, Counter> vilkårIkkeOppfylt = new HashMap<>();
    private final Map<VilkårType, Map<String, Counter>> vilkårsUtfall = new HashMap<>();
    private final Counter antallSøknaderMottatt = Metrics.counter("soknad.kontantstotte.funksjonell.antallsoknader");

    private final HashMap<String, Counter> barnehagestatus = new HashMap<>();
    private final HashMap<String, Counter> boddEllerJobbetINorgeEllerEøsIFemÅr = new HashMap<>();
    private final Counter mottarKontantstotteFraUtlandet = Metrics.counter("soknad.kontantstotte.funksjonell.mottarKontantstotteFraUtlandet", "status", "JA");

    public FunksjonelleMetrikker() {
        Arrays.stream(UtfallType.values()).forEach(utfallType -> søkerUtfall.put(
                utfallType.name(),
                Metrics.counter("soknad.kontantstotte.behandling.funksjonell.utfall", "status", utfallType.name(), "beskrivelse", utfallType.getBeskrivelse())
        ));

        Arrays.stream(VilkårIkkeOppfyltÅrsak.values()).forEach(vilkårIkkeOppfyltÅrsak -> vilkårIkkeOppfylt.put(
                Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()),
                Metrics.counter("soknad.kontantstotte.behandling.funksjonell.avslag", "status", Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()), "beskrivelse", vilkårIkkeOppfyltÅrsak.getBeskrivelse())
        ));

        Arrays.stream(Barnehageplass.BarnehageplassVerdier.values()).forEach(barnehageplassVerdi -> barnehagestatus.put(
                barnehageplassVerdi.name(),
                Metrics.counter("soknad.kontantstotte.funksjonell.barnehage", "status", barnehageplassVerdi.name(), "beskrivelse", barnehageplassVerdi.getBeskrivelse())
        ));

        Arrays.stream(TilknytningTilUtland.TilknytningTilUtlandVerdier.values()).forEach(tilknytningTilUtlandVerdi -> boddEllerJobbetINorgeEllerEøsIFemÅr.put(
                tilknytningTilUtlandVerdi.name(),
                Metrics.counter("soknad.kontantstotte.funksjonell.boddEllerJobbetINorgeEllerEøsIFemÅr", "status", tilknytningTilUtlandVerdi.name(), "beskrivelse", tilknytningTilUtlandVerdi.getBeskrivelse())
        ));

        Arrays.stream(VilkårType.values())
                .forEach(v -> {
                    final var vilkårUtfallTypeMap = new HashMap<String, Counter>();
                    Arrays.stream(VilkårIkkeOppfyltÅrsak.values()).filter(it -> it.getVilkårType().equals(v)).forEach(årsak ->
                            vilkårUtfallTypeMap.put(Integer.toString(årsak.getÅrsakKode()), Metrics.counter("soknad.kontantstotte.funksjonell.vilkar." + v.getKode(), "status", årsak.getKode(), "beskrivelse", årsak.getBeskrivelse())));
                    Arrays.stream(VilkårOppfyltÅrsak.values()).forEach(årsak ->
                            vilkårUtfallTypeMap.put(Integer.toString(årsak.getÅrsakKode()), Metrics.counter("soknad.kontantstotte.funksjonell.vilkar." + v.getKode(), "status", årsak.getKode(), "beskrivelse", årsak.getBeskrivelse())));
                    vilkårsUtfall.put(v, vilkårUtfallTypeMap);
                });
    }

    public void tellFunksjonelleMetrikker(Søknad søknad, Vedtak vedtak) {
        antallSøknaderMottatt.increment();

        final var vilkårvurdering = vedtak.getVilkårvurdering();
        final Counter samletUtfall = søkerUtfall.get(vilkårvurdering.getSamletUtfallType().name());
        if (samletUtfall != null) {
            søkerUtfall.get(vilkårvurdering.getSamletUtfallType().name()).increment();
        } else {
            log.info("Fant ikke counter for samlet utfall: " + vilkårvurdering.getSamletUtfallType().name());
        }

        vilkårvurdering.getResultater().forEach(r -> {
            if (r.getUtfallType().equals(UtfallType.IKKE_OPPFYLT)) {
                final Counter counter = vilkårIkkeOppfylt.get(r.getUtfallÅrsak().getKode());
                if (counter != null) {
                    counter.increment();
                } else {
                    log.info("Fant ikke counter for årsak til ikke oppfylt vilkår: " + r.getUtfallÅrsak().getKode());
                }
            }
        });

        barnehagestatus.get(søknad.barnehageplass.barnBarnehageplassStatus.name()).increment();
        boddEllerJobbetINorgeEllerEøsIFemÅr.get(søknad.tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar.name()).increment();

        if (søknad.utenlandskKontantstotte.mottarKontantstotteFraUtlandet.equals(Standpunkt.JA.getKode())) {
            mottarKontantstotteFraUtlandet.increment();
        }
    }
}
