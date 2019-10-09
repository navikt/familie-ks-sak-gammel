package no.nav.familie.ks.sak.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Sporingsdata {

    private Map<SporingsloggId, String> verdier = new TreeMap<>(Comparator.comparing(SporingsloggId::getSporingsloggKode));

    private Sporingsdata() {
    }

    private Sporingsdata(Map<SporingsloggId, String> verdier) {
        this.verdier.putAll(verdier);
    }

    public static Sporingsdata opprett() {
        return new Sporingsdata();
    }

    public Sporingsdata kopi() {
        return new Sporingsdata(verdier);
    }

    public Sporingsdata leggTilId(SporingsloggId navn, Long verdi) {
        String verdiStr = (verdi != null ? verdi.toString() : "");
        return leggTilId(navn, verdiStr);
    }

    public Sporingsdata leggTilId(SporingsloggId navn, String verdi) {
        verdier.put(navn, verdi);
        return this;
    }

    public Set<SporingsloggId> getNøkler() {
        return verdier.keySet();
    }

    public String getVerdi(SporingsloggId navn) {
        return verdier.get(navn);
    }


    @Override
    public String toString() {
        return "Sporingsdata{" +
               ", verdier=" + verdier +
               '}';
    }
}
