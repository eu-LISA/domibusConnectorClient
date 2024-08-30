/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem.isupport;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * The ISupportFSConfiguration class is a configuration class for the iSupport file system storage
 * within the Domibus system.
 */
@Configuration
@EnableConfigurationProperties(ISupportFSMessageProperties.class)
@Component
@ConditionalOnProperty(
    prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX,
    name = DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue = "true"
)
@Profile("iSupport")
@PropertySource("classpath:/connector-client-fs-isupport-message.properties")
public class ISupportFSConfiguration {
}
