package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "GR_PERSONOPPLYSNINGER")
public class PersonopplysningGrunnlag extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GR_PERSONOPPLYSNINGER")
    private Long id;

    @Column(name = "behandling_id", updatable = false, nullable = false)
    private Long behandlingId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId oppgittAnnenPart;

    @Column(name = "aktiv", nullable = false)
    private Boolean aktiv = true;

    @ManyToOne
    @JoinColumn(name = "registerinformasjon_id", updatable = false)
    private PersonopplysningerInformasjon registrertePersonopplysninger;

    PersonopplysningGrunnlag() {
    }

    PersonopplysningGrunnlag(Behandling behandling, AktørId oppgittAnnenPart, PersonopplysningerInformasjon registrertePersonopplysninger) {
        this.behandlingId = behandling.getId();
        this.oppgittAnnenPart = oppgittAnnenPart;
        this.registrertePersonopplysninger = registrertePersonopplysninger;
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


    public Optional<PersonopplysningerInformasjon> getRegisterVersjon() {
        return Optional.ofNullable(registrertePersonopplysninger);
    }

    public Optional<AktørId> getOppgittAnnenPart() {
        return Optional.ofNullable(oppgittAnnenPart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonopplysningGrunnlag that = (PersonopplysningGrunnlag) o;
        return Objects.equals(behandlingId, that.behandlingId) &&
            Objects.equals(oppgittAnnenPart, that.oppgittAnnenPart) &&
            Objects.equals(registrertePersonopplysninger, that.registrertePersonopplysninger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, oppgittAnnenPart, registrertePersonopplysninger);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonopplysningGrunnlagEntitet{");
        sb.append("id=").append(id);
        sb.append(", søknadAnnenPart=").append(oppgittAnnenPart);
        sb.append(", aktiv=").append(aktiv);
        sb.append(", registrertePersonopplysninger=").append(registrertePersonopplysninger);
        sb.append('}');
        return sb.toString();
    }
}
