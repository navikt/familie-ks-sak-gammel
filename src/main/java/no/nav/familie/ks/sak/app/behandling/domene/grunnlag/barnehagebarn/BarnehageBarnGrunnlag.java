package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;

@Entity
@Table(name = "GR_BARNEHAGE_BARN")
public class BarnehageBarnGrunnlag extends BaseEntitet<Long> {

    @ManyToOne
    @JoinColumn(name = "OPPGITT_FAMILIEFORHOLD_ID")
    private OppgittFamilieforhold familieforhold;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;


}
