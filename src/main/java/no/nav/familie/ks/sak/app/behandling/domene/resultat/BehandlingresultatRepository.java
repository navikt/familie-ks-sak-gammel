package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BehandlingresultatRepository extends JpaRepository<BehandlingResultat, Long> {

    @Query(value="SELECT br FROM BehandlingResultat br WHERE br.behandlingId = :behandlingsId and br.aktiv = true")
    BehandlingResultat finnBehandlingsresultat(Long behandlingsId);
}
