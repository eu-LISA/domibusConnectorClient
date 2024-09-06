/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.standard;

import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import javax.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

/**
 * The DomibusConnectorClientFSStandardConfiguration class is a configuration class for the Domibus
 * connector client file system storage. It is responsible for creating beans that are necessary for
 * the connector client to handle file system storage of messages.
 *
 * @see DomibusConnectorClientFSStorageConfiguration
 * @see DomibusConnectorClientFSMessageProperties
 */
@Configuration
@ConditionalOnProperty(
    prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX,
    name = DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue = "true",
    matchIfMissing = true
)
@PropertySource("classpath:/connector-client-fs-default-message.properties")
@Validated
@Valid
@EnableConfigurationProperties(DomibusConnectorClientFSMessageProperties.class)
public class DomibusConnectorClientFSStandardConfiguration {
}
