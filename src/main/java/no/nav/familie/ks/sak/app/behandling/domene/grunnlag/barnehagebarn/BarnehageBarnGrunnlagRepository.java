package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BarnehageBarnGrunnlagRepository extends JpaRepository<BarnehageBarnGrunnlag, Long> {

    @Query(value="SELECT * FROM GR_BARNEHAGE_BARN b WHERE b.behandling_id = :behandlingsId and b.aktiv = true", nativeQuery = true)
    BarnehageBarnGrunnlag finnGrunnlag(Long behandlingsId);
}
