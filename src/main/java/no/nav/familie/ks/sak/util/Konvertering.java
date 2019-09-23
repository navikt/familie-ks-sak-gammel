package no.nav.familie.ks.sak.util;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;

public final class Konvertering {
    public static boolean konverterTilBoolean(String kode) {
        return Standpunkt.map(kode, Standpunkt.UBESVART).equals(Standpunkt.JA);
    }
}
