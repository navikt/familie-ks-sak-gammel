package no.nav.familie.ks.sak.util;

import no.nav.familie.ks.kontrakter.søknad.Standpunkt;

import java.util.Optional;

public final class Konvertering {
    public static boolean konverterTilBoolean(String kode) {
        return Optional.of(Standpunkt.valueOf(kode)).orElse(Standpunkt.UBESVART) == Standpunkt.JA;
    }
}
