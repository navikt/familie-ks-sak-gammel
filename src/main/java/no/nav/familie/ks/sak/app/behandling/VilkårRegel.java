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
 * Regeltjeneste som fastsetter uttaksperioder som er søkt om for svangerskapspenger.
 */
@RuleDocumentation(value = VilkårRegel.ID, specificationReference = "TODO")
public class VilkårRegel implements RuleService<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4";

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

        var sjekkMedlemsskap = rs.hvisRegel(SjekkMedlemsskap.ID, "Har forelder/foreldre vært medlem av den norske forlketrygden minimum fem år?")
                .hvis(new SjekkMedlemsskap(), Sluttpunkt.ikkeOppfylt("UT8010", VilkårIkkeOppfyltÅrsak.IKKE_FEM_ÅR_MEDLEMSKAP))
                .ellers(Sluttpunkt.oppfylt("UT8011", VilkårOppfyltÅrsak.VILKÅR_OPPFYLT));

        var sjekkTreÅrUtland = rs.hvisRegel(SjekkTreÅrUtland.ID, "Har forelder svart ja på planer om tre måneders opphold i utlandet de neste 12 månedene?")
                .hvis(new SjekkTreÅrUtland(), Sluttpunkt.ikkeOppfylt("UT8009", VilkårIkkeOppfyltÅrsak.HAR_SVART_TRE_MÅNEDER_UTLAND))
                .ellers(sjekkMedlemsskap);

        var sjekkDeltBosted = rs.hvisRegel(SjekkDeltBosted.ID, "Har foreldre avtalt delt bosted for barnet?")
                .hvis(new SjekkDeltBosted(), Sluttpunkt.ikkeOppfylt("UT8009", VilkårIkkeOppfyltÅrsak.FORELDRE_HAR_AVTALT_DELT_BOSTED))
                .ellers(sjekkTreÅrUtland);

        var sjekkBarnSammeAdresse = rs.hvisRegel(SjekkBarnSammeAdresse.ID, "Bor forelder sammen med barnet?")
                .hvis(new SjekkBarnSammeAdresse(), Sluttpunkt.ikkeOppfylt("UT8009", VilkårIkkeOppfyltÅrsak.FORELDER_BOR_IKKE_MED_BARN))
                .ellers(sjekkDeltBosted);

        return rs.hvisRegel(SjekkBarnetsAlder.ID, "Er barnet i kontantstøtte-alder?")
                .hvis(new SjekkBarnetsAlder(), Sluttpunkt.ikkeOppfylt("UT8004", VilkårIkkeOppfyltÅrsak.IKKE_GYLDIG_ALDER))
                .ellers(sjekkBarnSammeAdresse);
    }

}
