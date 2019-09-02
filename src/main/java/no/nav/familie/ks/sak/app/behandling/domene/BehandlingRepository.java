package no.nav.familie.ks.sak.app.behandling.domene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BehandlingRepository extends JpaRepository<Behandling, Long> {

    @Query(value="SELECT b, f FROM Behandling b JOIN b.fagsak f WHERE f.id = :fagsakId")
    List<Behandling> finnBehandlinger(Long fagsakId);
}
