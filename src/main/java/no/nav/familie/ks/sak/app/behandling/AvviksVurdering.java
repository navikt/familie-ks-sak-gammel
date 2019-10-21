package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.avvik.AvvikResultat;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AvvikType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.avvik.personIkkeFunnet.regel.PersonIkkeFunnet;
import no.nav.familie.ks.sak.app.behandling.vilkår.SamletVurdering;
import no.nav.fpsak.nare.evaluation.Evaluation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvviksVurdering implements SamletVurdering {

    private Map<AvvikType, Evaluation> avvik;

    public AvviksVurdering() {
        this.avvik = Collections.singletonMap(AvvikType.AVVIK_PERSON_IKKE_FUNNET, new PersonIkkeFunnet().evaluate(null));
    }

    public List<AvvikResultat> getResultater() {
        return avvik.entrySet().stream()
            .map(entry -> new AvvikResultat(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public UtfallType getSamletUtfallType() {
        return UtfallType.MANUELL_BEHANDLING;
    }

    public Map<AvvikType, Evaluation> getAvvik() {
        return avvik;
    }
}
