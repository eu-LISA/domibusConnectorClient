package eu.domibus.connector.client.ui.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import reactor.core.publisher.Mono;

@Controller
public class VaadingConnectorClientUIServiceClient
{

	@Resource(name="restClient")
	private WebClient client;

	public DomibusConnectorClientMessageList getAllMessages() {

		DomibusConnectorClientMessageList messagesList = this.client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/getAllMessages")
						.build())
				.retrieve()
				.bodyToMono(DomibusConnectorClientMessageList.class)
				.onErrorStop()
				.block();


		return messagesList;
	}

	public DomibusConnectorClientMessage getMessageById(Long id) throws ConnectorClientServiceClientException 
	{
		try {
			DomibusConnectorClientMessage message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessageById")
							.queryParam("id", id)
							.build(id))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessage.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public DomibusConnectorClientMessage getMessageByBackendMessageId(String backendMessageId) throws ConnectorClientServiceClientException {
		try {
			DomibusConnectorClientMessage message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessageByBackendMessageId")
							.queryParam("backendMessageId", backendMessageId)
							.build(backendMessageId))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessage.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public DomibusConnectorClientMessage getMessageByEbmsId(String ebmsId) throws ConnectorClientServiceClientException {
		try {
			DomibusConnectorClientMessage message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessageByEbmsMessageId")
							.queryParam("ebmsMessageId", ebmsId)
							.build(ebmsId))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessage.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public DomibusConnectorClientMessageList getMessagesByPeriod(Date fromDate, Date toDate) throws ConnectorClientServiceClientException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		try {
			DomibusConnectorClientMessageList message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessagesByPeriod")
							.queryParam("from", sdf.format(fromDate))
							.queryParam("to", sdf.format(toDate))
							.build(sdf.format(fromDate), sdf.format(toDate)))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessageList.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) throws ConnectorClientServiceClientException {
		try {
			DomibusConnectorClientMessageList message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessagesByConversationId")
							.queryParam("conversationId", conversationId)
							.build(conversationId))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessageList.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public byte[] loadFileContentFromStorageLocation (String storageLocation, String fileName) {
		Mono<byte[]> result = this.client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/loadFileContentFromStorage")
						.queryParam("storageLocation", storageLocation)
						.queryParam("fileName", fileName)
						.build(storageLocation, fileName))
				.exchange()
				.flatMap(response -> response.bodyToMono(ByteArrayResource.class))
				.map(ByteArrayResource::getByteArray);
		return result.block();
	}

	public DomibusConnectorClientMessage saveMessage(DomibusConnectorClientMessage message) {
		Mono<DomibusConnectorClientMessage> bodyToMono = this.client.post()
				.uri("/saveMessage")
				.body(Mono.just(message), DomibusConnectorClientMessage.class)
				.retrieve()
				.bodyToMono(DomibusConnectorClientMessage.class);
		return bodyToMono.block();
	}

	public boolean uploadFileToMessage(DomibusConnectorClientMessageFile messageFile) {
		Mono<Boolean> bodyToMono = this.client.post()
				.uri("/uploadMessageFile")
				.body(Mono.just(messageFile), DomibusConnectorClientMessageFile.class)
				.retrieve()
				.bodyToMono(Boolean.class);
		return bodyToMono.block();
	}

	public boolean deleteFileFromMessage(DomibusConnectorClientMessageFile messageFile) {
		Mono<Boolean> bodyToMono = this.client.post()
				.uri("/deleteMessageFile")
				.body(Mono.just(messageFile), DomibusConnectorClientMessageFile.class)
				.retrieve()
				.bodyToMono(Boolean.class);
		return bodyToMono.block();
	}

	public void deleteMessageById(Long id) throws ConnectorClientServiceClientException {
		
		try{
			Mono<Boolean> bodyToMono = this.client.post()
				.uri("/deleteMessageById")
				.body(Mono.just(id), Long.class)
				.retrieve()
				.bodyToMono(Boolean.class);
		bodyToMono.block();
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

	public boolean submitStoredMessage(DomibusConnectorClientMessage message) throws ConnectorClientServiceClientException {
		try {
			Mono<Boolean> bodyToMono = this.client.post()
					.uri("/submitStoredClientMessage")
					.body(Mono.just(message), DomibusConnectorClientMessage.class)
					.retrieve()
					.bodyToMono(Boolean.class)
					.onErrorStop();
			return bodyToMono.block();
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
	}

}
