package no.nav.familie.ks.sak.app.integrasjon.personopplysning.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.familie.ks.sak.app.integrasjon.felles.ws.sts.NAVSTSClient;
import no.nav.familie.ks.sak.app.integrasjon.felles.ws.sts.StsConfigurationUtil;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConsumerProducer {
    private PersonConsumerConfig consumerConfig;


    public PersonConsumerProducer(PersonConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @Bean
    public PersonConsumer personConsumer() {
        PersonV3 port = wrapWithSts(consumerConfig.getPort(), NAVSTSClient.StsClientType.SYSTEM_SAML);
        return new PersonConsumer(port);
    }

    private PersonV3 wrapWithSts(PersonV3 port, NAVSTSClient.StsClientType samlTokenType) {
        return StsConfigurationUtil.wrapWithSts(port, samlTokenType);
    }

}
