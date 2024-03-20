package eu.domibus.connector.client.link.ws.configuration;


import java.util.Properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;


@ConfigurationProperties(prefix = ConnectorLinkWSProperties.PREFIX)
@Validated
@Valid
public class ConnectorLinkWSProperties {

    private static final Logger LOGGER = LogManager.getLogger(ConnectorLinkWSProperties.class);

    public static final String PREFIX = "connector-client.connector-link.ws";
    public static final String ENABLED_PROPERTY_NAME = "enabled";
    public static final String PUSH_ENABLED_PROPERTY_NAME = "pushEnabled";

    /**
     * Boolean as String value. May be "true" or "false". 
     * If left empty "false" is assumed.
     * Indicates if the client should offer a webservice for the domibusConnector to push messages to the client.
     * If set, the client libraries should run in a web container, or the client application in standalone mode.
     */
    private boolean pushEnabled;

    /**
     * Boolean as String value. May be "true" or "false". 
     * If left empty "true" is assumed.
     * Indicates if the client starts the webservice-client for the domibusConnector's backend webservice.
     */
    private boolean enabled;

    /**
     * The URL of the domibusConnector. More specific the DomibusConnectorBackendWebService address.
     */
    @NotNull
    private String connectorAddress;

    /**
     * Adress of the push webservice. 
     * Relativ path of the webservice the client offers for the domibusConnector in case the push mode is enabled.
     */
    @NotNull
    private String publishAddress = "/domibusConnectorDeliveryWebservice";

    
    /**
     * Definition xml of the webservice-security. 
     * By default the library offers the file "wsdl/backend.policy.xml" on the classpath that matches the settings used by the domibusConnector.
     */
    private Resource wsPolicy = new ClassPathResource("wsdl/backend.policy.xml");

    @NestedConfigurationProperty
    @NotNull
    private CxfTrustKeyStoreConfigurationProperties cxf;

//    @NestedConfigurationProperty
//    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;


    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public String getConnectorAddress() {
        return connectorAddress;
    }

    public void setConnectorAddress(String connectorAddress) {
        this.connectorAddress = connectorAddress;
    }

    public String getPublishAddress() {
        return publishAddress;
    }

    public void setPublishAddress(String publishAddress) {
        this.publishAddress = publishAddress;
    }

    public Resource getWsPolicy() {
        return wsPolicy;
    }

    public void setWsPolicy(Resource wsPolicy) {
        this.wsPolicy = wsPolicy;
    }

    public CxfTrustKeyStoreConfigurationProperties getCxf() {
        return cxf;
    }

    public void setCxf(CxfTrustKeyStoreConfigurationProperties cxf) {
        this.cxf = cxf;
    }

    /**
     * Maps the configured properties for key-/truststore and keys to the crypto Properties
     *  also see https://ws.apache.org/wss4j/config.html
     * @return the wss Properties
     */
    public Properties getWssProperties() {
        Properties p = mapCertAndStoreConfigPropertiesToMerlinProperties();
        LOGGER.debug("getSignatureProperties() are: [{}]", p);
        return p;
    }


    /**
     * Maps the own configured properties to the crypto Properties
     *  also see https://ws.apache.org/wss4j/config.html
     * @return the wss Properties
     */
    public Properties mapCertAndStoreConfigPropertiesToMerlinProperties() {
        Properties p = new Properties();
        p.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks"); //TODO: also set type by config!
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", this.getCxf().getKeyStore().getPassword());
        LOGGER.debug("setting [org.apache.wss4j.crypto.merlin.keystore.file={}]", this.getCxf().getKeyStore().getPath());
        try {
            p.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", this.getCxf().getKeyStore().getPathUrlAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error with property: [" + PREFIX + ".key.store.path]\n" +
                    "value is [" + this.getCxf().getKeyStore().getPath() + "]");
        }
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", this.getCxf().getPrivateKey().getAlias());
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.private.password", this.getCxf().getPrivateKey().getPassword());
        p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password", this.getCxf().getTrustStore().getPassword());
        try {
            LOGGER.debug("setting [org.apache.wss4j.crypto.merlin.truststore.file={}]", getCxf().getTrustStore().getPath());
            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", getCxf().getTrustStore().getPathUrlAsString());
        } catch (Exception e) {
            LOGGER.info("Trust Store Property: [" + PREFIX + ".trust.store.path]" +
                            "\n cannot be processed. Using the configured key store [{}] as trust store",
                    p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));

            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));
            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password", p.getProperty("org.apache.wss4j.crypto.merlin.keystore.password"));
        }
//        p.setProperty("org.apache.wss4j.crypto.merlin.load.cacerts", Boolean.toString(getCxf().isLoadCaCerts()));

        return p;
    }
}
