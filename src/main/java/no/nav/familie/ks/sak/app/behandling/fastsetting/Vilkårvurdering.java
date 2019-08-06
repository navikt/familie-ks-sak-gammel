package no.nav.familie.ks.sak.app.behandling.fastsetting;

import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårÅrsak;

public class Vilkårvurdering {

    private UtfallType utfallType = UtfallType.UAVKLART;
    private VilkårÅrsak vilkårÅrsak;
    private String regelInput;
    private String regelSporing;

    public Vilkårvurdering() { }

    public VilkårÅrsak getVilkårÅrsak() {
        return vilkårÅrsak;
    }

    public String getRegelInput() {
        return regelInput;
    }

    public String getRegelSporing() {
        return regelSporing;
    }

    public UtfallType getUtfallType() { return utfallType; }


    public static final class Builder {
        private Vilkårvurdering kladd;

        public Builder() {
            kladd = new Vilkårvurdering();
        }


        public Vilkårvurdering.Builder medVilkårÅrsak(VilkårÅrsak vilkårÅrsak) {
            kladd.vilkårÅrsak = vilkårÅrsak;
            return this;
        }

        public Vilkårvurdering.Builder medInputJson(String regelInput) {
            kladd.regelInput = regelInput;
            return this;
        }

        public Vilkårvurdering.Builder medRegelJson(String regelSporing) {
            kladd.regelSporing = regelSporing;
            return this;
        }

        public Vilkårvurdering.Builder medUtfallType(UtfallType utfallType) {
            kladd.utfallType = utfallType;
            return this;
        }

        public Vilkårvurdering build() {
            return kladd;
        }
    }
}


