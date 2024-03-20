package eu.domibus.connector.client.scheduler.job;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.lib.spring.configuration.types.DomibusConnectorDuration;

@Component
@ConfigurationProperties(prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX)
@Validated
@Valid
public class GetMessagesFromConnectorJobConfigurationProperties {

    public static final String PREFIX = "connector-client.scheduler.get-messages-from-connector-job";

    @NestedConfigurationProperty
    @NotNull
    private DomibusConnectorDuration repeatInterval;

    /**
     * Boolean as String value. May be "true" or "false". 
     * Enables the timer-triggered job to get messages from the domibusConnector via pull mode.
     * Obsolete, if push mode is enabled.
     */
    private boolean enabled;
    
    private int maxFetchCount;
    
    private boolean autoAcknowledgeMessages = true;

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

	public int getMaxFetchCount() {
		return maxFetchCount;
	}

	public void setMaxFetchCount(int maxFetchCount) {
		this.maxFetchCount = maxFetchCount;
	}

	public boolean isAutoAcknowledgeMessages() {
		return autoAcknowledgeMessages;
	}

	public void setAutoAcknowledgeMessages(boolean autoAcknowledgeMessages) {
		this.autoAcknowledgeMessages = autoAcknowledgeMessages;
	}
}
