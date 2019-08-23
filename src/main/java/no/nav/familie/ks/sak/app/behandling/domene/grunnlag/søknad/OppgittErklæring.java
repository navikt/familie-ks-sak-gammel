package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_ERKLAERING")
public class OppgittErklæring extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_erklaering_seq")
    @SequenceGenerator(name = "so_erklaering_seq")
    private Long id;

    @Column(name = "BARN_HJEMME", nullable = false, updatable = false)
    private boolean barnetHjemmeværendeOgIkkeAdoptert;

    @Column(name = "BOR_SAMMEN_MED_BARNET", nullable = false, updatable = false)
    private boolean borSammenMedBarnet;

    @Column(name = "IKKE_AVTALT_DELT_BOSTED", nullable = false, updatable = false)
    private boolean ikkeAvtaltDeltBosted;

    @Column(name = "BARN_I_NORGE", nullable = false, updatable = false)
    private boolean barnINorgeNeste12Måneder;

    OppgittErklæring() {
    }

    public OppgittErklæring(boolean barnetHjemmeværendeOgIkkeAdoptert, boolean borSammenMedBarnet, boolean ikkeAvtaltDeltBosted, boolean barnINorgeNeste12Måneder) {
        this.barnetHjemmeværendeOgIkkeAdoptert = barnetHjemmeværendeOgIkkeAdoptert;
        this.borSammenMedBarnet = borSammenMedBarnet;
        this.ikkeAvtaltDeltBosted = ikkeAvtaltDeltBosted;
        this.barnINorgeNeste12Måneder = barnINorgeNeste12Måneder;
    }

    public boolean isBarnetHjemmeværendeOgIkkeAdoptert() {
        return barnetHjemmeværendeOgIkkeAdoptert;
    }

    public boolean isBorSammenMedBarnet() {
        return borSammenMedBarnet;
    }

    public boolean isIkkeAvtaltDeltBosted() {
        return ikkeAvtaltDeltBosted;
    }

    public boolean isBarnINorgeNeste12Måneder() {
        return barnINorgeNeste12Måneder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OppgittErklæring that = (OppgittErklæring) o;
        return barnetHjemmeværendeOgIkkeAdoptert == that.barnetHjemmeværendeOgIkkeAdoptert &&
                borSammenMedBarnet == that.borSammenMedBarnet &&
                ikkeAvtaltDeltBosted == that.ikkeAvtaltDeltBosted &&
                barnINorgeNeste12Måneder == that.barnINorgeNeste12Måneder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), barnetHjemmeværendeOgIkkeAdoptert, borSammenMedBarnet, ikkeAvtaltDeltBosted, barnINorgeNeste12Måneder);
    }

    @Override
    public String toString() {
        return "OppgittErklæring{" +
                "id=" + id +
                "barnetHjemmeværendeOgIkkeAdoptert=" + barnetHjemmeværendeOgIkkeAdoptert +
                ", borSammenMedBarnet=" + borSammenMedBarnet +
                ", ikkeAvtaltDeltBosted=" + ikkeAvtaltDeltBosted +
                ", barnINorgeNeste12Måneder=" + barnINorgeNeste12Måneder +
                '}';
    }
}