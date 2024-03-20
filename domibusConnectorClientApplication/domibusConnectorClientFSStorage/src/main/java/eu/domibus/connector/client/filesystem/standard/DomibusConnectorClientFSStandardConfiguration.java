package eu.domibus.connector.client.filesystem.standard;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Configuration
@ConditionalOnProperty(prefix=DomibusConnectorClientFSStorageConfiguration.PREFIX, name=DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue="true", matchIfMissing = true)
@PropertySource("classpath:/connector-client-fs-default-message.properties")
@Validated
@Valid
@EnableConfigurationProperties(DomibusConnectorClientFSMessageProperties.class)
public class DomibusConnectorClientFSStandardConfiguration {
}
