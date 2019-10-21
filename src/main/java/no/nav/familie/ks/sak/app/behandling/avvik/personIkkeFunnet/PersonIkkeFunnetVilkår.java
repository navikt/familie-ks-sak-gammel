package no.nav.familie.ks.sak.app.behandling.avvik.personIkkeFunnet;

import no.nav.familie.ks.sak.app.behandling.avvik.AvvikRegel;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AvvikType;
import no.nav.familie.ks.sak.app.behandling.avvik.personIkkeFunnet.regel.PersonIkkeFunnet;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;

import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.specification.Specification;

import static no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak.AVVIK_PERSON_IKKE_FUNNET;

public class PersonIkkeFunnetVilkår implements AvvikRegel<Faktagrunnlag> {


    @Override
    public AvvikType getAvvikType() {
        return AvvikType.AVVIK_PERSON_IKKE_FUNNET;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Specification<V> getSpecification() {
        final var rs = new Ruleset<Faktagrunnlag>();
        return rs.hvisRegel(PersonIkkeFunnet.ID, "Avvik: Klarte ikke innhente personopplysninger for søker, medforelder og/eller barn")
            .hvis(new PersonIkkeFunnet(), Sluttpunkt.ikkeOppfylt(AVVIK_PERSON_IKKE_FUNNET))
            .ellers(Sluttpunkt.ikkeOppfylt(AVVIK_PERSON_IKKE_FUNNET));
    }
}
