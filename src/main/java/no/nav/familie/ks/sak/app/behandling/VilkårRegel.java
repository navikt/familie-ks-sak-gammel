package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.regler.*;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårOppfyltÅrsak;
import no.nav.fpsak.nare.RuleService;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;

/**
 * Regeltjeneste som fastsetter kontantstøttevedtak og om  krav til automatisk behandling er oppfylt
 */
@RuleDocumentation(value = VilkårRegel.ID, specificationReference = "TODO")
public class VilkårRegel implements RuleService<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";

    private final Ruleset<Faktagrunnlag> rs = new Ruleset<>();

    public VilkårRegel() {
        // For dokumentasjonsgenerering
    }

    @Override
    public Evaluation evaluer(Faktagrunnlag grunnlag) {
        return getSpecification().evaluate(grunnlag);
    }

    @Override
    public Specification<Faktagrunnlag> getSpecification() {

        Specification<Faktagrunnlag> sjekkMedlemsskap = rs.hvisRegel(SjekkMedlemsskap.ID, "Har forelder/foreldre vært medlem av den norske forlketrygden minimum fem år?")
                .hvis(new SjekkMedlemsskap(), Sluttpunkt.ikkeOppfylt("UTFALLKODE1", VilkårIkkeOppfyltÅrsak.IKKE_FEM_ÅR_MEDLEMSKAP))
                .ellers(Sluttpunkt.oppfylt("UTFALLKODE2", VilkårOppfyltÅrsak.VILKÅR_OPPFYLT));

        return rs.hvisRegel(SjekkBarnehage.ID, "Har barnet/har barnet hatt barnehageplass?")
                .hvis(new SjekkBarnehage(), Sluttpunkt.ikkeOppfylt("UTFALLKODE3", VilkårIkkeOppfyltÅrsak.BARNEHAGEPLASS))
                .ellers(sjekkMedlemsskap);
    }

}
