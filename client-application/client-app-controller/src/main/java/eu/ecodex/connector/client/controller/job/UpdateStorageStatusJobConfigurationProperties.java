/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.job;

import eu.domibus.connector.lib.spring.configuration.types.DomibusConnectorDuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * The {@code UpdateStorageStatusJobConfigurationProperties} class is a configuration class that
 * holds properties related to the Update Storage Status Job in the Domibus Connector Client
 * module.
 *
 * @see DomibusConnectorDuration
 */
@Data
@Valid
@Component
@Validated
@ConfigurationProperties(prefix = UpdateStorageStatusJobConfigurationProperties.PREFIX)
public class UpdateStorageStatusJobConfigurationProperties {
    public static final String PREFIX = "connector-client.controller.update-storage-status-job";
    @NestedConfigurationProperty
    @NotNull
    private DomibusConnectorDuration repeatInterval;
    private boolean enabled;
}
