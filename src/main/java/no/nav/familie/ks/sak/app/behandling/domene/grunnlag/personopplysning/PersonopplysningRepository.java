package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonopplysningRepository extends JpaRepository<PersonopplysningGrunnlag, Long> {
}
