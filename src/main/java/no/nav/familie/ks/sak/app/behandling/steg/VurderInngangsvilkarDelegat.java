package no.nav.familie.ks.sak.app.behandling.steg;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class VurderInngangsvilkarDelegat implements JavaDelegate {

    Logger logger = LoggerFactory.getLogger(VurderInngangsvilkarDelegat.class);

    public void execute(DelegateExecution execution) throws Exception {
        Random random = new Random();
        logger.info("Vurderer inngangsvilkår '" + execution.getActivityInstanceId() + "'...");
        execution.setVariable("godkjent", random.nextBoolean());
    }

}