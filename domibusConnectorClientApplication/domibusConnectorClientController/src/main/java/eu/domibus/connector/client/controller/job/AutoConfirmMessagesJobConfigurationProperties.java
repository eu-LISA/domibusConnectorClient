package eu.domibus.connector.client.controller.job;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.lib.spring.configuration.types.DomibusConnectorDuration;

@Component
@ConfigurationProperties(prefix = AutoConfirmMessagesJobConfigurationProperties.PREFIX)
@Validated
@Valid
public class AutoConfirmMessagesJobConfigurationProperties {
	
	public static final String PREFIX = "connector-client.controller.auto-confirm-messages-job";
	
	@NestedConfigurationProperty
    @NotNull
    private DomibusConnectorDuration repeatInterval;

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
