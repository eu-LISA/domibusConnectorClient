/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.rest.impl;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.rest.DomibusConnectorClientDeliveryRestClientAPI;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * The DomibusConnectorClientDeliveryRestClient class is used to deliver new messages and
 * confirmations from a connector client to a backend application via REST API.
 */
public class DomibusConnectorClientDeliveryRestClient
    implements DomibusConnectorClientDeliveryRestClientAPI {
    private WebClient deliveryRestClient;
    private String deliverNewMessageMethodUrl;
    private String deliverNewConfirmationMethodUrl;

    @Override
    public void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage msg)
        throws Exception {
        try {
            Mono<Boolean> bodyToMono =
                this.deliveryRestClient.post()
                                       .uri(deliverNewMessageMethodUrl)
                                       .body(Mono.just(msg), DomibusConnectorClientMessage.class)
                                       .retrieve()
                                       .bodyToMono(Boolean.class);
            bodyToMono.block();
        } catch (WebClientResponseException e) {
            throw new Exception(e.getResponseBodyAsString());
        }
    }

    @Override
    public void deliverNewConfirmationFromConnectorClientToBackend(
        DomibusConnectorClientMessage newMessage) throws Exception {
        try {
            Mono<Boolean> bodyToMono = this.deliveryRestClient.post()
                                                              .uri(deliverNewConfirmationMethodUrl)
                                                              .body(
                                                                  Mono.just(newMessage),
                                                                  DomibusConnectorMessageType.class
                                                              )
                                                              .retrieve()
                                                              .bodyToMono(Boolean.class);
            bodyToMono.block();
        } catch (WebClientResponseException e) {
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
