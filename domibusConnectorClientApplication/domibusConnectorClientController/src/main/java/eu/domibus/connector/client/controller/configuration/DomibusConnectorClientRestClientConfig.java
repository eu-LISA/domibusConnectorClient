package eu.domibus.connector.client.controller.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import eu.domibus.connector.client.controller.rest.impl.DomibusConnectorClientDeliveryRestClient;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = DomibusConnectorClientRestClientConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
//@ConditionalOnProperty(prefix = DomibusConnectorClientRestClientConfig.PREFIX, value = "enabled", havingValue = "true")
public class DomibusConnectorClientRestClientConfig {
	
	public static final String PREFIX = DomibusConnectorClientControllerConfig.PREFIX + ".delivery-rest-client";
	
	@NotNull
	private String url;
	
	@NotNull
	private boolean enabled;
	
	@NotNull
	private String deliverNewMessageMethodUrl;
	
	@NotNull
	private String deliverNewConfirmationMethodUrl;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl(url).build();
	}
	
	@Bean
	public DomibusConnectorClientDeliveryRestClient deliveryRestClient() {
		if(!enabled)
			return null;
		DomibusConnectorClientDeliveryRestClient restClient = new DomibusConnectorClientDeliveryRestClient();
		restClient.setDeliveryRestClient(webClient(WebClient.builder()));
		restClient.setDeliverNewConfirmationMethodUrl(deliverNewConfirmationMethodUrl);
		restClient.setDeliverNewMessageMethodUrl(deliverNewMessageMethodUrl);
		
		return restClient;
	}

	public String getDeliverNewMessageMethodUrl() {
		return deliverNewMessageMethodUrl;
	}

	public void setDeliverNewMessageMethodUrl(String deliverNewMessageMethodUrl) {
		this.deliverNewMessageMethodUrl = deliverNewMessageMethodUrl;
	}

	public String getDeliverNewConfirmationMethodUrl() {
		return deliverNewConfirmationMethodUrl;
	}

	public void setDeliverNewConfirmationMethodUrl(String deliverNewConfirmationMethodUrl) {
		this.deliverNewConfirmationMethodUrl = deliverNewConfirmationMethodUrl;
	}

}
