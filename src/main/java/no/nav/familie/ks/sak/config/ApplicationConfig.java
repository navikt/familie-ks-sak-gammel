package no.nav.familie.ks.sak.config;

import no.nav.familie.ks.sak.app.behandling.steg.ArkiverVedtakDelegat;
import no.nav.familie.ks.sak.app.behandling.steg.GenererVedtaksBrevDelegat;
import no.nav.familie.ks.sak.app.behandling.steg.HentSaksopplysningerDelegat;
import no.nav.familie.ks.sak.app.behandling.steg.VurderInngangsvilkarDelegat;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootConfiguration
@EnableProcessApplication
@ComponentScan({ "no.nav.familie.ks.sak" })
public class ApplicationConfig {

    @Autowired
    private RuntimeService runtimeService;

    public void initKontantstotte(PostDeployEvent event) {
        event.getProcessEngine().getRepositoryService()
                .createDeployment()
                .addModelInstance("kontantstotte.bpmn", sokOmKontantstotte())
                .name("kontantstotte")
                .deploy();
    }

    @EventListener
    public void onStart(PostDeployEvent event) {
        initKontantstotte(event);
        runtimeService.startProcessInstanceByKey("kontantstotte");

    }

    @Bean
    public BpmnModelInstance sokOmKontantstotte() {

        BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("kontantstotte")
                .name("Søk om kontantstøtte prosess")
                .startEvent()
                .name("Søknad mottatt")

                .serviceTask()
                .name("Hent saksopplysninger")
                .camundaClass(HentSaksopplysningerDelegat.class)

                .serviceTask()
                .id("nare")
                .name("Vurder inngangsvilkår")
                .camundaClass(VurderInngangsvilkarDelegat.class)

                .exclusiveGateway()
                .name("Inngangsvilkår godkjent?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("ja", "${godkjent}")

                .serviceTask()
                .name("Generer vedtaksbrev")
                .id("genbrev")
                .camundaClass(GenererVedtaksBrevDelegat.class)

                .serviceTask()
                .name("Arkiver vedtaksbrev")
                .camundaClass(ArkiverVedtakDelegat.class)

                .endEvent()
                .name("Søknad innvilget")

                .moveToLastGateway()
                .condition("nei", "${!godkjent}")

                .userTask()
                .name("Manuell saksbehandling")
                .camundaAssignee("demo")

                .exclusiveGateway()
                .name("Innvilget?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("nei", "${!godkjent}")

                .endEvent()
                .name("Søknad avslått")

                .moveToLastGateway()
                .condition("ja", "${godkjent}")
                .connectTo("genbrev")
                .done();

        return modelInstance;
    }
}
