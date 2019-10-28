package no.nav.familie.ks.sak.app.behandling.avvik;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AvvikType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.fpsak.nare.evaluation.Evaluation;

public class AvvikResultat {
    private final Evaluation evaluation;
    private final AvvikType avvikType;

    public AvvikResultat(AvvikType avvikType, Evaluation evaluation) {
        this.avvikType = avvikType;
        this.evaluation = evaluation;
    }

    public UtfallType getUtfallType() {
        return UtfallType.MANUELL_BEHANDLING;
    }

    public AvvikType getAvvikType() {
        return avvikType;
    }
}
