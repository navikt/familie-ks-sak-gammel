package no.nav.familie.ks.sak.app.behandling.steg;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArkiverVedtakDelegat implements JavaDelegate {

    Logger logger = LoggerFactory.getLogger(ArkiverVedtakDelegat.class);

    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Arkiverer vedtaksbrev '" + execution.getActivityInstanceId() + "'...");

    }

}