package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SøknadGrunnlagRepository extends JpaRepository<SøknadGrunnlag, Long> {

    @Query(value="SELECT * FROM GR_SOKNAD s WHERE s.behandling_id = :behandlingsId", nativeQuery = true)
    SøknadGrunnlag finnGrunnlag(Long behandlingsId);
}
