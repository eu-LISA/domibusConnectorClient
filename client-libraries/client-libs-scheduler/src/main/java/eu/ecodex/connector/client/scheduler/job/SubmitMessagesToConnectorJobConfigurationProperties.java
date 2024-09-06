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
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class represents the configuration properties for SubmitMessagesToConnectorJob.
 * It is used to configure the job's behavior and triggers.
 */
@Data
@Component
@ConfigurationProperties(prefix = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX)
public class SubmitMessagesToConnectorJobConfigurationProperties {
    public static final String PREFIX =
        "connector-client.scheduler.submit-messages-to-connector-job";
    private DomibusConnectorDuration repeatInterval;
    /**
     * Boolean as String value. May be "true" or "false". Enables the timer-triggered job to search
     * for new messages to submit via the DomibusConnectorBackendClient.
     */
    private boolean enabled;
}
