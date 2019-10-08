package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonopplysningGrunnlagRepository extends JpaRepository<PersonopplysningGrunnlag, Long> {

    @Query("SELECT gr FROM PersonopplysningGrunnlag gr WHERE behandlingId = ?1 AND aktiv = true")
    Optional<PersonopplysningGrunnlag> findByBehandlingAndAktiv(Long behandlingId);

    @Query(value="SELECT gr FROM PersonopplysningGrunnlag gr WHERE behandlingId = ?1")
    PersonopplysningGrunnlag finnGrunnlag(Long behandlingsId);
}
