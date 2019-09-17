package no.nav.familie.ks.sak.app.behandling.fastsetting;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingException;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FastsettingService {
    private RegisterInnhentingService registerInnhentingService;
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;

    @Autowired
    FastsettingService(RegisterInnhentingService registerInnhentingService, SøknadGrunnlagRepository søknadGrunnlagRepository, BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository) {
        this.registerInnhentingService = registerInnhentingService;
        this.søknadGrunnlagRepository = søknadGrunnlagRepository;
        this.barnehageBarnGrunnlagRepository = barnehageBarnGrunnlagRepository;
    }

    public Faktagrunnlag fastsettFakta(Behandling behandling, Søknad søknad) throws RegisterInnhentingException {
        SøknadGrunnlag søknadGrunnlag = søknadGrunnlagRepository.finnGrunnlag(behandling.getId());
        BarnehageBarnGrunnlag barnehageBarnGrunnlag = barnehageBarnGrunnlagRepository.finnGrunnlag(behandling.getId());
        TpsFakta tpsFakta = registerInnhentingService.innhentPersonopplysninger(behandling, søknad);

        return new Faktagrunnlag.Builder()
            .medBarnehageBarnGrunnlag(barnehageBarnGrunnlag)
            .medSøknadGrunnlag(søknadGrunnlag)
            .medTpsFakta(tpsFakta)
            .build();
    }
}
