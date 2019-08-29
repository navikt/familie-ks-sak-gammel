package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BehandlingresultatRepository extends JpaRepository<BehandlingResultat, Long> {

    @Query(value="SELECT * FROM BEHANDLING_RESULTAT br WHERE br.behandling_id = :behandlingsId", nativeQuery = true)
    BehandlingResultat finnBehandlingsresultat(Long behandlingsId);
}
