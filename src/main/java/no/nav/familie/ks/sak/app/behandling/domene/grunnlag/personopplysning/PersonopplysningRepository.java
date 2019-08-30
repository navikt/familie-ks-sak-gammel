package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface PersonopplysningRepository extends JpaRepository<PersonopplysningGrunnlag, Long> {

    @Query("SELECT gr FROM PersonopplysningGrunnlag WHERE behandlingId = ?1 AND aktiv = true")
    Optional<PersonopplysningGrunnlag> findByBehandlingAndAktiv(Long behandlingId);
}
