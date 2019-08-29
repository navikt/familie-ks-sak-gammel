package no.nav.familie.ks.sak.app.behandling.domene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BehandlingRepository extends JpaRepository<Behandling, Long> {

    @Query(value="SELECT * FROM BEHANDLING b WHERE b.fagsak_id = :fagsakId", nativeQuery = true)
    List<Behandling> finnBehandlinger(@Param("fagsakId") Long fagsakId);
}
