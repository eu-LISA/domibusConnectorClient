/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.link;

import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import java.util.List;

/**
 * This Interface class is an abstraction layer between the domibusConnectorClientWSLink module and
 * the domibusConnectorClientLibrary. It delegates methods through the WSLink to the
 * domibusConnectorAPI backend.
 *
 * @author riederb
 */
public interface DomibusConnectorClientLink {
    /**
     * Delegate method to request new messages via domibusConnectorAPI from the backend of the
     * domibusConnector to the domibusConnectorClientLibrary.
     *
     * @return the {@link DomibusConnectorMessagesType} holding all messages which are pending for
     *      this client.
     * @throws DomibusConnectorBackendWebServiceClientException if an error occurs while requesting
     *                                                          messages from the connector
     */
    DomibusConnectorMessagesType requestMessagesFromConnector()
        throws DomibusConnectorBackendWebServiceClientException;

    /**
     * Delegate method to submit a message from the domibusConnectorClientLibrary to the backend of
     * the domibusConnector.
     *
     * @param message the message to be submitted to the connector
     * @return the result of the submission
     * @throws DomibusConnectorBackendWebServiceClientException if an error occurs while submitting
     *                                                          the message to the connector
     */
    DomibsConnectorAcknowledgementType submitMessageToConnector(
        DomibusConnectorMessageType message)
        throws DomibusConnectorBackendWebServiceClientException;

    /**
     * Returns a list of pending message transport IDs.
     *
     * @return the list of pending message transport IDs
     * @throws DomibusConnectorBackendWebServiceClientException if an error occurs while retrieving
     *                                                          the pending messages
     */
    List<String> listPendingMessages() throws DomibusConnectorBackendWebServiceClientException;

    /**
     * Requests the message for the given message transport ID. Transport ID is received when
     * calling listPendingMessages.
     *
     * @param messageTransportId the message transport ID of the message to retrieve
     * @return the message
     * @throws DomibusConnectorBackendWebServiceClientException if an error occurs while retrieving
     *                                                          the message
     */
    DomibusConnectorMessageType getMessageById(String messageTransportId)
        throws DomibusConnectorBackendWebServiceClientException;

    /**
     * If a received message is completely processed by the client the message can be acknowledged
     * to the domibusConnector. In case of positive result, the domibusConnector finishes the
     * message. In case of negative result, the domibusConnector moves the message to its internal
     * DLQ.
     *
     * @param result of type {@link DomibusConnectorMessageResponseType} MUST contain message
     *               transport ID as responseForMessageId!
     * @throws DomibusConnectorBackendWebServiceClientException if an error occurs while
     *                                                          acknowledging the message
     */
    void acknowledgeMessage(DomibusConnectorMessageResponseType result)
        throws DomibusConnectorBackendWebServiceClientException;
}
