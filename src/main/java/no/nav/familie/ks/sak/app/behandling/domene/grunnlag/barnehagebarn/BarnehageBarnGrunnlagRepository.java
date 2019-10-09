package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BarnehageBarnGrunnlagRepository extends JpaRepository<BarnehageBarnGrunnlag, Long> {

    @Query(value="SELECT b FROM BarnehageBarnGrunnlag b WHERE b.behandlingId = :behandlingsId and b.aktiv = true")
    Optional<BarnehageBarnGrunnlag> finnGrunnlag(Long behandlingsId);
}
