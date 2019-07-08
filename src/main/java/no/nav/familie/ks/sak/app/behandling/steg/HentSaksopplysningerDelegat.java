package no.nav.familie.ks.sak.app.behandling.steg;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class HentSaksopplysningerDelegat implements JavaDelegate {

    Logger logger = LoggerFactory.getLogger(HentSaksopplysningerDelegat.class);

    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Henter Saksopplysninger '" + execution.getActivityInstanceId() + "'...");
        Random random = new Random();

        if (random.nextBoolean()) {
            execution.createIncident("feil","Random feil for å teste");
        }
    }

}