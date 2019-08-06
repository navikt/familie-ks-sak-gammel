package no.nav.familie.ks.sak;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FunksjonelleMetrikker {

    private final HashMap<String, Counter> barnehageCounters = Maps.newHashMap();
    private final Counter mottarKontantstotteFraUtlandet = Metrics.counter("soknad.kontantstotte.funksjonell.mottarKontantstotteFraUtlandet", "status", "JA");
    private final Counter boddEllerJobbetINorgeMinstFemAar = Metrics.counter("soknad.kontantstotte.funksjonell.boddEllerJobbetINorgeMinstFemAar", "status", "JA");
    private final Counter ikkeBoddEllerJobbetINorgeMinstFemAar = Metrics.counter("soknad.kontantstotte.funksjonell.ikkeBoddEllerJobbetINorgeMinstFemAar", "status", "JA");


    public FunksjonelleMetrikker() {
        Lists.newArrayList(Barnehageplass.BarnehageplassVerdier.values()).forEach(barnehageplassVerdi -> barnehageCounters.put(
                barnehageplassVerdi.name(),
                Metrics.counter("soknad.kontantstotte.funksjonell.barnehage", "status", barnehageplassVerdi.name())
        ));
    }

    public void tellFunksjonelleMetrikker(Søknad søknad) {
        barnehageCounters.get(søknad.barnehageplass.barnBarnehageplassStatus.name()).increment();

        if (søknad.utenlandskKontantstotte.mottarKontantstotteFraUtlandet.equals("JA")) {
            mottarKontantstotteFraUtlandet.increment();
        }

        if (søknad.tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar.equals("JA")) {
            boddEllerJobbetINorgeMinstFemAar.increment();
        } else {
            ikkeBoddEllerJobbetINorgeMinstFemAar.increment();
        }
    }
}
