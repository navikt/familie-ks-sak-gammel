package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.RuleService;

public interface InngangsvilkårRegel<T> extends RuleService<T> {

    VilkårType getVilkårType();

    T konverterInput(Faktagrunnlag faktagrunnlag);

}
