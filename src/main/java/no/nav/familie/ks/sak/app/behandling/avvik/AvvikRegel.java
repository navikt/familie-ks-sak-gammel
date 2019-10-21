package no.nav.familie.ks.sak.app.behandling.avvik;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AvvikType;
import no.nav.fpsak.nare.RuleService;

public interface AvvikRegel<T> extends RuleService<T> {

    AvvikType getAvvikType();
}
