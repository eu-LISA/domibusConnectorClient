/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.rest;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This REST interface allows a backend application that is connected to the domibusConnectorClient
 * to request messages and confirmations that have been received by the domibusConnectorClient.
 *
 * @author riederb
 */
@RequestMapping(DomibusConnectorClientMessageRestAPI.MESSAGERESTSERVICE_PATH)
public interface DomibusConnectorClientMessageRestAPI {
    /**
     * This static String is the relative path where this REST service can be reached.
     */
    String MESSAGERESTSERVICE_PATH = "/messagerestservice";
    /**
     * This static String is the relative path where this method of this REST service can be
     * reached.
     */
    String REQUEST_REJECTED_OR_CONFIRMED_MESSAGES_FROM_CONNECTOR_CLIENT =
        "/requestRejectedOrConfirmedMessagesFromConnectorClient";
    /**
     * This static String is the relative path where this method of this REST service can be
     * reached.
     */
    String REQUEST_NEW_MESSAGES_FROM_CONNECTOR_CLIENT =
        "/requestNewMessagesFromConnectorClient";

    /**
     * Returns all messages that have been successfully received and processed by the
     * domibusConnectorClient. Therefore, the message status of the messages requested by this
     * method is RECEIVED. After collecting those messages, the message status changes to
     * DELIVERED_BACKEND.
     *
     * @return all messages with status RECEIVED.
     */
    @GetMapping(REQUEST_NEW_MESSAGES_FROM_CONNECTOR_CLIENT)
    DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient();

    /**
     * Returns all messages and their confirmations attached with the message status
     * REJECTED or CONFIRMED. In those states, a confirmation have been received from the
     * domibusConnector. After collecting the messages, the status of those messages changes to
     * CONFIRMATION_DELIVERED_BACKEND.
     *
     * @return all messages with their confirmations with the status REJECTED or CONFIRMED.
     */
    @GetMapping(REQUEST_REJECTED_OR_CONFIRMED_MESSAGES_FROM_CONNECTOR_CLIENT)
    DomibusConnectorClientMessageList requestRejectedOrConfirmedMessagesFromConnectorClient();
}
