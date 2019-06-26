package no.nav.familie.ks.sak.behandling.soknad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Barnehageplass {
    public String harBarnehageplass;
    public String barnBarnehageplassStatus;
    public String harBarnehageplassAntallTimer;
    public String harBarnehageplassDato;
    public String harBarnehageplassKommune;
    public String harSluttetIBarnehageKommune;
    public String harSluttetIBarnehageAntallTimer;
    public String harSluttetIBarnehageDato;
    public List<VedleggMetadata> harSluttetIBarnehageVedlegg;
    public String skalBegynneIBarnehageKommune;
    public String skalBegynneIBarnehageAntallTimer;
    public String skalBegynneIBarnehageDato;
    public String skalSlutteIBarnehageKommune;
    public String skalSlutteIBarnehageAntallTimer;
    public String skalSlutteIBarnehageDato;
    public List<VedleggMetadata> skalSlutteIBarnehageVedlegg;

    public Barnehageplass() {
        this.harSluttetIBarnehageVedlegg = new ArrayList<>();
        this.skalSlutteIBarnehageVedlegg = new ArrayList<>();
    }

    public List<VedleggMetadata> getRelevanteVedlegg() {
        // TODO: Implementer uthenting av vedlegg basert på valg
        return Stream.of(harSluttetIBarnehageVedlegg, skalSlutteIBarnehageVedlegg)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
