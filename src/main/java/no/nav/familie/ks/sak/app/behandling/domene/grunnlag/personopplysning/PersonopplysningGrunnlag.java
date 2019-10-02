package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "GR_PERSONOPPLYSNINGER")
public class PersonopplysningGrunnlag extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GR_PERSONOPPLYSNINGER_SEQ")
    private Long id;

    @Column(name = "behandling_id", updatable = false, nullable = false)
    private Long behandlingId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "oppgitt_annen_part", updatable = false)))
    private AktørId oppgittAnnenPart;

    @Column(name = "aktiv", nullable = false)
    private Boolean aktiv = true;

    @OneToMany(mappedBy = "personopplysningGrunnlag", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Person> personer = new ArrayList<>();

    public PersonopplysningGrunnlag() {
    }
    public PersonopplysningGrunnlag(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public PersonopplysningGrunnlag(Long behandlingId, List<Person> personer) {
        this.behandlingId = behandlingId;
        this.personer = personer;
    }

    /**
     * Kun synlig for abstract test scenario
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    Long getBehandlingId() {
        return behandlingId;
    }

    void setAktiv(final boolean aktiv) {
        this.aktiv = aktiv;
    }


    public Optional<List<Person>> getRegistrertePersoner() {
        return Optional.ofNullable(personer);
    }

    public Optional<AktørId> getOppgittAnnenPart() {
        return Optional.ofNullable(oppgittAnnenPart);
    }


    public void leggTilPerson(Person person) {
        person.setPersonopplysningGrunnlag(this);
        personer.add(person);
    }

    public Person getPerson(PersonType personType) {
        for (Person p : personer) {
            if (p.getType().equals(personType)){
                return p;
            }
        }
        return null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonopplysningGrunnlag that = (PersonopplysningGrunnlag) o;
        return Objects.equals(behandlingId, that.behandlingId) &&
            Objects.equals(oppgittAnnenPart, that.oppgittAnnenPart) &&
            Objects.equals(personer, that.personer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, oppgittAnnenPart, personer);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonopplysningGrunnlagEntitet{");
        sb.append("id=").append(id);
        sb.append(", søknadAnnenPart=").append(oppgittAnnenPart);
        sb.append(", aktiv=").append(aktiv);
        sb.append('}');
        return sb.toString();
    }
}
