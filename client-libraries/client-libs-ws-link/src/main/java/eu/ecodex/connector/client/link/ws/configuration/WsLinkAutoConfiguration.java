/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.link.ws.configuration;

import static eu.ecodex.connector.client.link.ws.configuration.ConnectorLinkWSProperties.PUSH_ENABLED_PROPERTY_NAME;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.connector.client.link.ws.DefaultWsCallbackHandler;
import eu.ecodex.connector.client.link.ws.WsPolicyLoader;
import eu.ecodex.connector.client.link.ws.impl.DomibusConnectorClientDeliveryWsImpl;
import eu.ecodex.connector.client.link.ws.impl.DomibusConnectorClientWSLinkImpl;
import eu.ecodex.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.ecodex.connector.ws.backend.webservice.DomibusConnectorBackendWSService;
import eu.ecodex.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.ecodex.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;
import jakarta.xml.ws.soap.SOAPBinding;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * The {@code WsLinkAutoConfiguration} class is responsible for auto-configuring and creating the
 * necessary beans for the WS Link module in Domibus. It is annotated with {@code @Configuration} to
 * indicate that it is a configuration class. The auto-configuration is conditionally enabled if the
 * {@code SpringBus} class is present on the classpath, and the {@code ConnectorLinkWSProperties}
 * class is enabled via configuration properties. This class is also dependent on the
 * {@code CxfAutoConfiguration} class being auto-configured first. The auto-configuration is further
 * conditional based on the property value for {@code enabled} in the prefix
 * {@code ConnectorLinkWSProperties.PREFIX}, with a default value of {@code true}.
 *
 * <p>This class has several nested classes. The nested class {@code SpringBusConfiguration} is
 * responsible for configuring and creating a {@code SpringBus} instance, which is a central class
 * in the Spring-based Apache CXF setup. This nested class is annotated with {@code @Configuration}
 * and {@code @ConditionalOnMissingBean({SpringBus.class})}, and it is imported as a resource using
 * the {@code @ImportResource} annotation.
 *
 * <p>The {@code WsLinkAutoConfiguration} class contains several bean methods annotated with
 * {@code @Bean} that create and configure beans for the WS Link module in Domibus. These beans
 * include {@code WsPolicyLoader}, {@code DomibusConnectorClientWSLinkImpl},
 * {@code DomibusConnectorBackendWebService}, {@code EndpointImpl}, and {@code Properties}. The
 * configurations for these beans include setting various properties on the beans such as addresses,
 * service names, endpoints, etc. The {@code DomibusConnectorBackendWebService} bean is dynamically
 * created using the {@code JaxWsProxyFactoryBean} class. Additionally, there is a private nested
 * class {@code ProcessMessageAfterReceivedFromConnectorInterceptor} that extends
 * {@code AbstractPhaseInterceptor} and is responsible for handling messages received from the
 * connector and processing them in the backend.
 *
 * <p>Note: This class does not provide example code, author, version, or since tags as per the
 * specified requirements. It also does not generate documentation for type member properties.
 */
@Configuration
@ConditionalOnClass(SpringBus.class)
@EnableConfigurationProperties(value = ConnectorLinkWSProperties.class)
@AutoConfigureAfter(CxfAutoConfiguration.class)
@ConditionalOnProperty(
    prefix = ConnectorLinkWSProperties.PREFIX, name = "enabled", matchIfMissing = true
)
public class WsLinkAutoConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(WsLinkAutoConfiguration.class);
    @Autowired
    private Bus cxfBus;
    @Autowired
    ConnectorLinkWSProperties connectorLinkWsProperties;

    /**
     * The SpringBusConfiguration class is a protected nested class within the
     * WsLinkAutoConfiguration class. It is a configuration class responsible for configuring and
     * creating a SpringBus instance, which is a central class in the Spring-based Apache CXF
     * setup.
     */
    @Configuration
    @ConditionalOnMissingBean({SpringBus.class})
    @ImportResource({"classpath:META-INF/cxf/cxf.xml"})
    protected static class SpringBusConfiguration {
        protected SpringBusConfiguration() {
        }
    }

    @Bean
    public WsPolicyLoader policyLoader() {
        return new WsPolicyLoader(connectorLinkWsProperties.getWsPolicy());
    }

    @Bean
    public DomibusConnectorClientWSLinkImpl domibusConnectorClientServiceWsImpl() {
        return new DomibusConnectorClientWSLinkImpl();
    }

    /**
     * Creates and configures a web service client for the Domibus connector backend.
     *
     * <p>The method sets up the necessary configurations and properties for the web service client
     * and creates an instance of the {@link DomibusConnectorBackendWebService} using the
     * {@link JaxWsProxyFactoryBean}.
     *
     * <p>The created web service client is registered and returned to the caller.
     *
     * @return the configured web service client for the Domibus connector backend
     */
    @Bean
    public DomibusConnectorBackendWebService connectorWsClient() {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendWebService.class);
        jaxWsProxyFactoryBean.setBus(cxfBus);
        jaxWsProxyFactoryBean.setAddress(connectorLinkWsProperties.getConnectorAddress());
        jaxWsProxyFactoryBean.setServiceName(DomibusConnectorBackendWSService.SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(
            DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
        jaxWsProxyFactoryBean.setWsdlURL(DomibusConnectorBackendWSService.WSDL_LOCATION.toString());
        jaxWsProxyFactoryBean.setBindingId(SOAPBinding.SOAP12HTTP_MTOM_BINDING);
        jaxWsProxyFactoryBean.getInInterceptors()
                             .add(new ProcessMessageAfterReceivedFromConnectorInterceptor());

        jaxWsProxyFactoryBean.getFeatures().add(policyLoader().loadPolicyFeature());

        if (jaxWsProxyFactoryBean.getProperties() == null) {
            jaxWsProxyFactoryBean.setProperties(new HashMap<>());
        }
        jaxWsProxyFactoryBean.getProperties().put("mtom-enabled", true);
        jaxWsProxyFactoryBean.getProperties().put(
            "security.encryption.properties",
            connectorWsLinkEncryptionProperties()
        );
        jaxWsProxyFactoryBean.getProperties().put(
            "security.encryption.username",
            connectorLinkWsProperties.getCxf()
                                     .getEncryptAlias()
        );
        jaxWsProxyFactoryBean.getProperties().put(
            "security.signature.properties",
            connectorWsLinkEncryptionProperties()
        );
        jaxWsProxyFactoryBean.getProperties()
                             .put("security.callback-handler", new DefaultWsCallbackHandler());

        DomibusConnectorBackendWebService domibusConnectorBackendWebService =
            jaxWsProxyFactoryBean.create(DomibusConnectorBackendWebService.class);
        LOGGER.debug("Registered WS Client for [{}]", DomibusConnectorBackendWebService.class);

        return domibusConnectorBackendWebService;
    }

    /**
     * Creates and publishes the Web Service endpoint for the Domibus connector delivery service.
     *
     * @return the created and published Web Service endpoint
     */
    @Bean
    @ConditionalOnProperty(
        prefix = ConnectorLinkWSProperties.PREFIX, value = PUSH_ENABLED_PROPERTY_NAME,
        matchIfMissing = false
    )
    public EndpointImpl domibusConnectorDeliveryServiceEndpoint() {
        var endpoint = new EndpointImpl(cxfBus, domibusConnectorClientDeliveryWsImpl());
        endpoint.setAddress(connectorLinkWsProperties.getPublishAddress());
        endpoint.setWsdlLocation(DomibusConnectorBackendDeliveryWSService.WSDL_LOCATION.toString());
        endpoint.setServiceName(DomibusConnectorBackendDeliveryWSService.SERVICE);
        endpoint.setEndpointName(
            DomibusConnectorBackendDeliveryWSService.DomibusConnectorBackendDeliveryWebService);

        var wsPolicyFeature = policyLoader().loadPolicyFeature();
        endpoint.getFeatures().add(wsPolicyFeature);

        endpoint.getProperties().put("mtom-enabled", true);
        endpoint.getProperties()
                .put("security.encryption.properties", connectorWsLinkEncryptionProperties());
        endpoint.getProperties()
                .put("security.signature.properties", connectorWsLinkEncryptionProperties());
        endpoint.getProperties().put("security.encryption.username", "useReqSigCert");

        endpoint.publish();
        LOGGER.debug(
            "Published WebService {} under {}", DomibusConnectorGatewayDeliveryWebService.class,
            endpoint.getPublishedEndpointUrl()
        );

        return endpoint;
    }

    /**
     * Returns the properties for configuring the encryption settings for the WS client connector.
     *
     * <p>The properties include the encryption provider, keystore and truststore details, and
     * other encryption specific configurations.
     *
     * @return the properties for configuring the encryption settings for the WS client connector
     */
    @Bean
    public Properties connectorWsLinkEncryptionProperties() {
        var properties = new Properties();

        CxfTrustKeyStoreConfigurationProperties cxf = connectorLinkWsProperties.getCxf();
        StoreConfigurationProperties cxfKeyStore = connectorLinkWsProperties.getCxf().getKeyStore();

        properties.put("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        properties.put("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.file", cxfKeyStore.getPathUrlAsString()
        );
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.password", cxfKeyStore.getPassword()
        );
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.alias", cxf.getPrivateKey().getAlias()
        );
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.private.password",
            cxf.getPrivateKey().getPassword()
        );

        properties.put("org.apache.wss4j.crypto.merlin.truststore.type", "jks");
        properties.put(
            "org.apache.wss4j.crypto.merlin.truststore.file",
            cxf.getTrustStore().getPathUrlAsString()
        );
        properties.put(
            "org.apache.wss4j.crypto.merlin.truststore.password",
            cxf.getTrustStore().getPassword()
        );

        return properties;
    }

    @Bean
    @ConditionalOnProperty(
        prefix = ConnectorLinkWSProperties.PREFIX, value = PUSH_ENABLED_PROPERTY_NAME
    )
    public DomibusConnectorClientDeliveryWsImpl domibusConnectorClientDeliveryWsImpl() {
        return new DomibusConnectorClientDeliveryWsImpl();
    }

    private class ProcessMessageAfterReceivedFromConnectorInterceptor
        extends AbstractPhaseInterceptor<Message> {
        ProcessMessageAfterReceivedFromConnectorInterceptor() {
            super(Phase.POST_INVOKE);
        }

        @Override
        public void handleMessage(Message message) {
            LOGGER.trace(
                "ProcessMessageAfterReceivedFromConnectorInterceptor: handleMessage: "
                    + "invoking backendSubmissionService.processMessageAfterDeliveredToBackend"
            );

            InputStream in = message.getContent(InputStream.class);
            byte[] payload;
            try {
                payload = IOUtils.readBytesFromStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var inputStream = new ByteArrayInputStream(payload);
            message.setContent(InputStream.class, inputStream);
        }
    }
}
