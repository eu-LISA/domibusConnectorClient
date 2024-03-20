package eu.domibus.connector.client.controller.rest.impl;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import eu.domibus.connector.client.rest.DomibusConnectorClientDeliveryRestClientAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import reactor.core.publisher.Mono;

//@Controller
//@ConditionalOnProperty(prefix = DomibusConnectorClientControllerRestClientConfig.PREFIX, value = "enabled", havingValue = "true")
public class DomibusConnectorClientDeliveryRestClient implements DomibusConnectorClientDeliveryRestClientAPI {

	private WebClient deliveryRestClient;
	
	private String deliverNewMessageMethodUrl;
	
	private String deliverNewConfirmationMethodUrl;
	

	@Override
	public void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage msg) throws Exception {
		try{
			Mono<Boolean> bodyToMono = this.deliveryRestClient.post()
				.uri(deliverNewMessageMethodUrl)
				.body(Mono.just(msg), DomibusConnectorClientMessage.class)
				.retrieve()
				.bodyToMono(Boolean.class);
		bodyToMono.block();
		}catch(WebClientResponseException e) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}

	@Override
	public void deliverNewConfirmationFromConnectorClientToBackend(DomibusConnectorClientMessage newMessage) throws Exception {
		try{
			Mono<Boolean> bodyToMono = this.deliveryRestClient.post()
				.uri(deliverNewConfirmationMethodUrl)
				.body(Mono.just(newMessage), DomibusConnectorMessageType.class)
				.retrieve()
				.bodyToMono(Boolean.class);
		bodyToMono.block();
		}catch(WebClientResponseException e) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}

	public WebClient getDeliveryRestClient() {
		return deliveryRestClient;
	}

	public void setDeliveryRestClient(WebClient deliveryRestClient) {
		this.deliveryRestClient = deliveryRestClient;
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
