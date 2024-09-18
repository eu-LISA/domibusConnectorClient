/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.scheduler.job;

import eu.domibus.connector.lib.spring.configuration.types.DomibusConnectorDuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the job that retrieves messages from the Domibus Connector.
 * This class holds the configuration settings for the job, including the interval at which
 * the job should run, whether the job is enabled, the maximum number of messages to fetch
 * per execution, and whether to automatically acknowledge messages upon retrieval.
 */
@Data
@Valid
@Validated
@Component
@ConfigurationProperties(prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX)
public class GetMessagesFromConnectorJobConfigurationProperties {
    public static final String PREFIX =
        "connector-client.scheduler.get-messages-from-connector-job";
    @NestedConfigurationProperty
    @NotNull
    private DomibusConnectorDuration repeatInterval;
    /**
     * Boolean as String value. May be "true" or "false". Enables the timer-triggered job to get
     * messages from the domibusConnector via pull mode. Obsolete, if push mode is enabled.
     */
    private boolean enabled;
    private int maxFetchCount;
    private boolean autoAcknowledgeMessages = true;
}

