package no.nav.familie.ks.sak.app.behandling.fastsetting;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.grunnlag.MedlFakta;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FastsettingService {
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;

    @Autowired
    FastsettingService(SøknadGrunnlagRepository søknadGrunnlagRepository, BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository) {
        this.søknadGrunnlagRepository = søknadGrunnlagRepository;
        this.barnehageBarnGrunnlagRepository = barnehageBarnGrunnlagRepository;
    }

    public Faktagrunnlag fastsettFakta(Behandling behandling, TpsFakta tpsFakta, MedlFakta medlFakta) {
        SøknadGrunnlag søknadGrunnlag = søknadGrunnlagRepository.finnGrunnlag(behandling.getId()).orElseThrow();
        BarnehageBarnGrunnlag barnehageBarnGrunnlag = barnehageBarnGrunnlagRepository.finnGrunnlag(behandling.getId()).orElseThrow();

        return new Faktagrunnlag.Builder()
            .medBarnehageBarnGrunnlag(barnehageBarnGrunnlag)
            .medSøknadGrunnlag(søknadGrunnlag)
            .medTpsFakta(tpsFakta)
            .medMedlFakta(medlFakta)
            .build();
    }
}
