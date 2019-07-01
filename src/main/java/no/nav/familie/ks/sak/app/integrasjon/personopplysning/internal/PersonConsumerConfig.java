package no.nav.familie.ks.sak.app.integrasjon.personopplysning.internal;


import javax.xml.namespace.QName;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import no.nav.familie.ks.sak.app.integrasjon.felles.ws.CallIdOutInterceptor;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
@EnableConfigurationProperties
public class PersonConsumerConfig implements EnvironmentAware {
    private static final String PERSON_V3_WSDL = "wsdl/no/nav/tjeneste/virksomhet/person/v3/Binding.wsdl";
    private static final String PERSON_V3_NAMESPACE = "http://nav.no/tjeneste/virksomhet/person/v3/Binding";
    private static final QName PERSON_V3_SERVICE = new QName(PERSON_V3_NAMESPACE, "Person_v3");
    private static final QName PERSON_V3_PORT = new QName(PERSON_V3_NAMESPACE, "Person_v3Port");

    private Environment environment;

    @Bean
    PersonV3 getPort() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(PERSON_V3_WSDL);
        factoryBean.setServiceName(PERSON_V3_SERVICE);
        factoryBean.setEndpointName(PERSON_V3_PORT);
        factoryBean.setServiceClass(PersonV3.class);
        factoryBean.setAddress(getEndpointUrl());
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(new CallIdOutInterceptor());
        return factoryBean.create(PersonV3.class);
    }

    String getEndpointUrl() {
        return environment.getRequiredProperty("integrasjon.person.url");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
