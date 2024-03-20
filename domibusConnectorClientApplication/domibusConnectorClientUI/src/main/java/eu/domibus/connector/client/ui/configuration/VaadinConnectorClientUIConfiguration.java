package eu.domibus.connector.client.ui.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VaadinConnectorClientUIConfiguration {
	
	@Value("${connector-client-rest-url}")
	private String connectorClientRestURL;
	
	@Bean
	public WebClient restClient(WebClient.Builder builder) {
		return builder.baseUrl(connectorClientRestURL).build();
	}
}
