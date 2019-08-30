package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.*;

@Entity(name = "PersonopplysningInformasjon")
@Table(name = "PO_INFORMASJON")
public class PersonInformasjon extends BaseEntitet {

    private static final String REF_NAME = "personopplysningInformasjon";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PO_INFORMASJON")
    private Long id;

    @OneToMany(mappedBy = REF_NAME)
    private List<PersonAdresse> adresser = new ArrayList<>();

    @OneToMany(mappedBy = REF_NAME)
    private List<Personopplysning> personopplysninger = new ArrayList<>();

    @OneToMany(mappedBy = REF_NAME)
    private List<PersonRelasjon> relasjoner = new ArrayList<>();

    @OneToMany(mappedBy = REF_NAME)
    private List<Statsborgerskap> statsborgerskap = new ArrayList<>();

    PersonInformasjon() {
    }

    PersonInformasjon(PersonInformasjon aggregat) {
        if (Optional.ofNullable(aggregat.getAdresser()).isPresent()) {
            aggregat.getAdresser()
                .forEach(e -> {
                    PersonAdresse entitet = new PersonAdresse(e);
                    adresser.add(entitet);
                    entitet.setPersonopplysningInformasjon(this);
                });
        }
        if (Optional.ofNullable(aggregat.getRelasjoner()).isPresent()) {
            aggregat.getRelasjoner()
                .forEach(e -> {
                    PersonRelasjon entitet = new PersonRelasjon(e);
                    relasjoner.add(entitet);
                    entitet.setPersonopplysningInformasjon(this);
                });
        }
        if (Optional.ofNullable(aggregat.getPersonopplysninger()).isPresent()) {
            aggregat.getPersonopplysninger()
                .forEach(e -> {
                    Personopplysning entitet = new Personopplysning(e);
                    personopplysninger.add(entitet);
                    entitet.setPersonopplysningInformasjon(this);
                });
        }
        if (Optional.ofNullable(aggregat.getStatsborgerskap()).isPresent()) {
            aggregat.getStatsborgerskap()
                .forEach(e -> {
                    Statsborgerskap entitet = new Statsborgerskap(e);
                    statsborgerskap.add(entitet);
                    entitet.setPersonopplysningInformasjon(this);
                });
        }
    }

    public PersonInformasjon leggTilAdresse(PersonAdresse adresse) {
        adresse.setPersonopplysningInformasjon(this);
        adresser.add(adresse);
        return this;
    }

    public PersonInformasjon leggTilPersonrelasjon(PersonRelasjon relasjon) {
        relasjon.setPersonopplysningInformasjon(this);
        this.relasjoner.add(relasjon);
        return this;
    }

    public PersonInformasjon leggTilPersonopplysning(Personopplysning personopplysning) {
        personopplysning.setPersonopplysningInformasjon(this);
        this.personopplysninger.add(personopplysning);
        return this;
    }

    public PersonInformasjon leggTilStatsborgerskap(Statsborgerskap statsborgerskap) {
        statsborgerskap.setPersonopplysningInformasjon(this);
        this.statsborgerskap.add(statsborgerskap);
        return this;
    }

    /**
     * Rydder bort alt unntatt personopplysninger
     */
    PersonInformasjon tilbakestill() {
        this.adresser.clear();
        this.relasjoner.clear();
        return this;
    }

    public List<PersonRelasjon> getRelasjoner() {
        return Collections.unmodifiableList(relasjoner);
    }

    public List<Personopplysning> getPersonopplysninger() {
        return Collections.unmodifiableList(personopplysninger);
    }

    public List<PersonAdresse> getAdresser() {
        return Collections.unmodifiableList(adresser);
    }

    public List<Statsborgerskap> getStatsborgerskap() {
        return Collections.unmodifiableList(statsborgerskap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonInformasjon that = (PersonInformasjon) o;
        return Objects.equals(adresser, that.adresser) &&
            Objects.equals(personopplysninger, that.personopplysninger) &&
            Objects.equals(relasjoner, that.relasjoner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adresser, personopplysninger, relasjoner);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonInformasjonEntitet{");
        sb.append("id=").append(id);
        sb.append(", adresser=").append(adresser);
        sb.append(", personopplysninger=").append(personopplysninger);
        sb.append(", relasjoner=").append(relasjoner);
        sb.append('}');
        return sb.toString();
    }
}
