package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SøknadGrunnlagRepository extends JpaRepository<SøknadGrunnlag, Long> {

    @Query(value="SELECT s FROM SøknadGrunnlag s WHERE s.behandlingId = :behandlingsId and s.aktiv = true")
    SøknadGrunnlag finnGrunnlag(Long behandlingsId);
}
