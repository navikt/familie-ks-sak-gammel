package no.nav.familie.ks.sak.app.behandling.domene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FagsakRepository extends JpaRepository<Fagsak, Long> {

    @Query(value="SELECT f FROM Fagsak f WHERE f.id = :fagsakId")
    Optional<Fagsak> finnFagsak(Long fagsakId);
}
