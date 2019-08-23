package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;

import javax.persistence.*;
import java.util.Set;

public class BehandlingResultat extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "behandling_resultat_seq")
    @SequenceGenerator(name = "behandling_resultat_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BEHANDLING_ID", nullable = false, updatable = false)
    private Behandling behandling;

    @OneToMany(mappedBy = "behandlingResultat")
    private Set<VilkårResultat> vilkårsResultat;


}
