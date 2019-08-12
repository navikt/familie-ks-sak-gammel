package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public interface VilkårÅrsak {

    default String getKode() {
        return "" + getÅrsakKode();
    }

    int getÅrsakKode();

    String getBeskrivelse();
}
