/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.service;

import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageList;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * The {@code VaadingConnectorClientUIServiceClient} class is a controller that interacts with the
 * Domibus Connector Client via the Vaadin Connector UI service.
 *
 * @see DomibusConnectorClientMessageList
 * @see DomibusConnectorClientMessage
 * @see ConnectorClientServiceClientException
 */
@Controller
public class VaadingConnectorClientUIServiceClient {
    @Resource(name = "restClient")
    private WebClient client;

    /**
     * Retrieves all messages from the Domibus Connector Client.
     *
     * @return DomibusConnectorClientMessageList - the list of messages.
     */
    public DomibusConnectorClientMessageList getAllMessages() {
        return this.client.get()
                          .uri(uriBuilder -> uriBuilder
                              .path("/getAllMessages")
                              .build()
                          )
                          .retrieve()
                          .bodyToMono(DomibusConnectorClientMessageList.class)
                          .onErrorStop()
                          .block();
    }

    /**
     * Retrieves a {@link DomibusConnectorClientMessage} by its ID from the Domibus Connector
     * Client.
     *
     * @param id the ID of the message to retrieve
     * @return the retrieved {@code DomibusConnectorClientMessage}
     * @throws ConnectorClientServiceClientException if an error occurs while retrieving the
     *                                               message
     */
    public DomibusConnectorClientMessage getMessageById(Long id)
        throws ConnectorClientServiceClientException {
        try {

            return this.client.get()
                              .uri(uriBuilder -> uriBuilder
                                  .path("/getMessageById")
                                  .queryParam("id", id)
                                  .build(id)
                              )
                              .retrieve()
                              .bodyToMono(DomibusConnectorClientMessage.class)
                              .onErrorStop()
                              .block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Retrieves a {@link DomibusConnectorClientMessage} from the Domibus Connector Client by its
     * backend message ID.
     *
     * @param backendMessageId the backend message ID of the message to retrieve
     * @return the retrieved {@code DomibusConnectorClientMessage}
     * @throws ConnectorClientServiceClientException if an error occurs while retrieving the
     *                                               message
     */
    public DomibusConnectorClientMessage getMessageByBackendMessageId(String backendMessageId)
        throws ConnectorClientServiceClientException {
        try {
            return this.client.get()
                              .uri(uriBuilder -> uriBuilder
                                  .path("/getMessageByBackendMessageId")
                                  .queryParam("backendMessageId", backendMessageId)
                                  .build(backendMessageId)
                              )
                              .retrieve()
                              .bodyToMono(DomibusConnectorClientMessage.class)
                              .onErrorStop()
                              .block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Retrieves a {@link DomibusConnectorClientMessage} by its EBMS ID from the Domibus Connector
     * Client.
     *
     * @param ebmsId the EBMS ID of the message to retrieve
     * @return the retrieved {@code DomibusConnectorClientMessage}
     * @throws ConnectorClientServiceClientException if an error occurs while retrieving the
     *                                               message
     */
    public DomibusConnectorClientMessage getMessageByEbmsId(String ebmsId)
        throws ConnectorClientServiceClientException {
        try {

            return this.client.get()
                              .uri(uriBuilder -> uriBuilder
                                  .path("/getMessageByEbmsMessageId")
                                  .queryParam("ebmsMessageId", ebmsId)
                                  .build(ebmsId))
                              .retrieve()
                              .bodyToMono(DomibusConnectorClientMessage.class)
                              .onErrorStop()
                              .block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Retrieves a list of messages from the Domibus Connector Client within the specified period.
     *
     * @param fromDate the start date of the period
     * @param toDate   the end date of the period
     * @return DomibusConnectorClientMessageList - the list of messages within the specified period
     * @throws ConnectorClientServiceClientException if an error occurs while retrieving the
     *                                               messages
     */
    public DomibusConnectorClientMessageList getMessagesByPeriod(Date fromDate, Date toDate)
        throws ConnectorClientServiceClientException {

        var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {

            return this.client.get()
                              .uri(uriBuilder -> uriBuilder
                                  .path("/getMessagesByPeriod")
                                  .queryParam("from", dateFormat.format(fromDate))
                                  .queryParam("to", dateFormat.format(toDate))
                                  .build(dateFormat.format(fromDate), dateFormat.format(toDate))
                              )
                              .retrieve()
                              .bodyToMono(DomibusConnectorClientMessageList.class)
                              .onErrorStop()
                              .block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Retrieves a list of messages from the Domibus Connector Client based on the given
     * conversation ID.
     *
     * @param conversationId the ID of the conversation for which messages are retrieved
     * @return DomibusConnectorClientMessageList - the list of messages retrieved
     * @throws ConnectorClientServiceClientException if an error occurs while retrieving the
     *                                               messages
     */
    public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId)
        throws ConnectorClientServiceClientException {
        try {
            return this.client.get()
                              .uri(uriBuilder -> uriBuilder
                                  .path("/getMessagesByConversationId")
                                  .queryParam("conversationId", conversationId)
                                  .build(conversationId)
                              )
                              .retrieve()
                              .bodyToMono(DomibusConnectorClientMessageList.class)
                              .onErrorStop()
                              .block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Retrieves the content of a file from the specified storage location.
     *
     * @param storageLocation the location of the file in storage
     * @param fileName        the name of the file to retrieve
     * @return the content of the file as a byte array
     */
    public byte[] loadFileContentFromStorageLocation(String storageLocation, String fileName) {
        Mono<byte[]> result = this.client.get()
                                         .uri(uriBuilder -> uriBuilder
                                             .path("/loadFileContentFromStorage")
                                             .queryParam("storageLocation", storageLocation)
                                             .queryParam("fileName", fileName)
                                             .build(storageLocation, fileName))
                                         .exchange()
                                         .flatMap(response -> response.bodyToMono(
                                             ByteArrayResource.class))
                                         .map(ByteArrayResource::getByteArray);
        return result.block();
    }

    /**
     * Save a message in the Domibus Connector Client.
     *
     * @param message the message to be saved
     * @return the saved message
     */
    public DomibusConnectorClientMessage saveMessage(DomibusConnectorClientMessage message) {
        var bodyToMono = this.client.post()
                                    .uri("/saveMessage")
                                    .body(Mono.just(message), DomibusConnectorClientMessage.class)
                                    .retrieve()
                                    .bodyToMono(DomibusConnectorClientMessage.class);
        return bodyToMono.block();
    }

    /**
     * Uploads a file to a message in the Domibus Connector Client.
     *
     * @param messageFile the {@code DomibusConnectorClientMessageFile} representing the file to
     *                    upload
     * @return {@code true} if the file was successfully uploaded, {@code false} otherwise
     */
    public boolean uploadFileToMessage(DomibusConnectorClientMessageFile messageFile) {
        var bodyToMono = this.client.post()
                                    .uri("/uploadMessageFile")
                                    .body(
                                        Mono.just(messageFile),
                                        DomibusConnectorClientMessageFile.class
                                    )
                                    .retrieve()
                                    .bodyToMono(Boolean.class);
        return bodyToMono.block();
    }

    /**
     * Deletes a file from a message in the Domibus Connector Client.
     *
     * @param messageFile the {@code DomibusConnectorClientMessageFile} representing the file to
     *                    delete
     * @return {@code true} if the file was successfully deleted, {@code false} otherwise
     */
    public boolean deleteFileFromMessage(DomibusConnectorClientMessageFile messageFile) {
        var bodyToMono = this.client.post()
                                    .uri("/deleteMessageFile")
                                    .body(
                                        Mono.just(messageFile),
                                        DomibusConnectorClientMessageFile.class
                                    )
                                    .retrieve()
                                    .bodyToMono(Boolean.class);
        return bodyToMono.block();
    }

    /**
     * Deletes a message from the Domibus Connector Client by its ID.
     *
     * @param id the ID of the message to delete
     * @throws ConnectorClientServiceClientException if an error occurs while deleting the message
     */
    public void deleteMessageById(Long id) throws ConnectorClientServiceClientException {
        try {
            var bodyToMono = this.client.post()
                                        .uri("/deleteMessageById")
                                        .body(Mono.just(id), Long.class)
                                        .retrieve()
                                        .bodyToMono(Boolean.class);
            bodyToMono.block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }

    /**
     * Submits a stored message to the Domibus Connector Client for processing.
     *
     * @param message the message to be submitted
     * @return {@code true} if the message was successfully submitted, {@code false} otherwise
     * @throws ConnectorClientServiceClientException if an error occurs while submitting the
     *                                               message
     */
    public boolean submitStoredMessage(DomibusConnectorClientMessage message)
        throws ConnectorClientServiceClientException {
        try {
            var bodyToMono = this.client.post()
                                        .uri("/submitStoredClientMessage")
                                        .body(
                                            Mono.just(message), DomibusConnectorClientMessage.class
                                        )
                                        .retrieve()
                                        .bodyToMono(Boolean.class)
                                        .onErrorStop();
            return bodyToMono.block();
        } catch (WebClientResponseException e) {
            throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
        }
    }
}
