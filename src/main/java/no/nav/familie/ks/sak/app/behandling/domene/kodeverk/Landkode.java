package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Landkode {

    public static final Landkode NORGE = new Landkode("NOR");
    public static final Landkode SVERIGE = new Landkode("SWE");
    private static final String UDEFINERT_KODE = "UDEFINERT";
    public static final Landkode UDEFINERT = new Landkode(UDEFINERT_KODE);

    @Column(name = "landkode", nullable = false, updatable = false)
    private String kode;

    Landkode() {
    }

    public Landkode(String kode) {
        Objects.requireNonNull(kode, "Landkode er påkrevd");
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    public boolean erNorge() {
        return NORGE.equals(this);
    }

    @Override
    public String toString() {
        return "Landkode{" +
            "kode='" + kode + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landkode landkode = (Landkode) o;
        return Objects.equals(kode, landkode.kode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode);
    }
}
