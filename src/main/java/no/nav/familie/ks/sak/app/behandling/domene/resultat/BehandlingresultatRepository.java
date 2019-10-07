package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BehandlingresultatRepository extends JpaRepository<BehandlingResultat, Long> {

    @Query(value="SELECT br FROM BehandlingResultat br WHERE br.behandlingId = :behandlingsId and br.aktiv = true")
    Optional<BehandlingResultat> finnBehandlingsresultat(Long behandlingsId);
}
