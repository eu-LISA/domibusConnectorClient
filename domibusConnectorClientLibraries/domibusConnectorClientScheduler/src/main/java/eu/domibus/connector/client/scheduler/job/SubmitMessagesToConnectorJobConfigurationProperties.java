package eu.domibus.connector.client.scheduler.job;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import eu.domibus.connector.lib.spring.configuration.types.DomibusConnectorDuration;

@Component
@ConfigurationProperties(prefix = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX)
public class SubmitMessagesToConnectorJobConfigurationProperties {

    public static final String PREFIX = "connector-client.scheduler.submit-messages-to-connector-job";

    private DomibusConnectorDuration repeatInterval;

    /**
     * Boolean as String value. May be "true" or "false". 
     * Enables the timer-triggered job to search for new messages to submit via the DomibusConnectorBackendClient.
     */
    private boolean enabled;

    public DomibusConnectorDuration getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(DomibusConnectorDuration repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
