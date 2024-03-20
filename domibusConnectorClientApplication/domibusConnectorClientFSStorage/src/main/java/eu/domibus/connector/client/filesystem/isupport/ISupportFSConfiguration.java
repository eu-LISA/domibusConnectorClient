package eu.domibus.connector.client.filesystem.isupport;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties(ISupportFSMessageProperties.class)
@Component
@ConditionalOnProperty(prefix= DomibusConnectorClientFSStorageConfiguration.PREFIX, name=DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue="true")
@Profile("iSupport")
@PropertySource("classpath:/connector-client-fs-isupport-message.properties")
public class ISupportFSConfiguration {



}
