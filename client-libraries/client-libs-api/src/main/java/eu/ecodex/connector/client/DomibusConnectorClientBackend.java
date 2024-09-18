/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client;

import eu.ecodex.connector.client.exception.DomibusConnectorClientBackendException;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This interface must be implemented if the domibusConnectorClientScheduler is used and/or the
 * client is set up in push/pull mode.
 *
 * @author Bernhard Rieder
 */
public interface DomibusConnectorClientBackend {
    /**
     * This method asks the backend of the client if new messages are to submit to the connector.
     * Must be implemented if domibusConnectorClientScheduler is used, or if the client
     * implementation is not self aware to recognize new messages at its backend.
     *
     * @return messages object holding a Collection of messages.
     * @throws DomibusConnectorClientBackendException If there is an error in the backend operations
     *                                                of the client while checking for new
     *                                                messages.
     */
    DomibusConnectorMessagesType checkClientForNewMessagesToSubmit()
        throws DomibusConnectorClientBackendException;

    /**
     * This method triggers the client's backend to store/put/forward messages received. Must be
     * implemented if domibusConnectorClientScheduler is used, or if the client is set up in
     * push/pull mode.
     *
     * @param message - The message object received from the connector.
     * @throws DomibusConnectorClientBackendException If there is an error in the backend operations
     *                                                of the client while delivering the message.
     */
    void deliverNewMessageToClientBackend(DomibusConnectorMessageType message)
        throws DomibusConnectorClientBackendException;

    /**
     * This method delivers a new acknowledgeable message to the backend of the client.
     *
     * @param message            - The message object received from the connector.
     * @param messageTransportId - The transport ID the connector gives a message.
     * @throws DomibusConnectorClientBackendException - If there is an error in the backend
     *                                                operations of the client while delivering the
     *                                                message.
     */
    void deliverNewAcknowledgeableMessageToClientBackend(
        DomibusConnectorMessageType message, String messageTransportId)
        throws DomibusConnectorClientBackendException;

    /**
     * This method triggers the client's backend to store/put/forward confirmation received. Must be
     * implemented if domibusConnectorClientScheduler is used, or if the client is set up in
     * push/pull mode.
     *
     * @param message - The message object containing the confirmation received from the connector.
     * @throws DomibusConnectorClientBackendException If there is an error in the backend operations
     *                                                of the client while delivering the
     *                                                confirmation.
     */
    void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
        throws DomibusConnectorClientBackendException;

    /**
     * This method triggers the client's backend to store/put/forward confirmation received. Must be
     * implemented if the message pulling with acknowledgement is used.
     *
     * @param message            - The message object containing the confirmation received from the
     *                           connector.
     * @param messageTransportId - The transport ID the connector gives a message.
     * @throws DomibusConnectorClientBackendException If there is an error in the backend operations
     *                                                of the client while delivering the
     *                                                acknowledgeable confirmation.
     */
    void deliverNewAcknowledgeableConfirmationToClientBackend(
        DomibusConnectorMessageType message, String messageTransportId)
        throws DomibusConnectorClientBackendException;

    /**
     * This method triggers the connector to generate and send a confirmation.
     *
     * @param originalMessage    - The original message the confirmation should be triggered for.
     * @param confirmationType   - The type of confirmation that should be triggered.
     * @param confirmationAction - The use-case specific action that is used for transmission of the
     *                           confirmation
     * @throws DomibusConnectorClientBackendException - If there is an error in the backend
     *                                                operations of the client while triggering the
     *                                                confirmation.
     */
    void triggerConfirmationForMessage(
        DomibusConnectorMessageType originalMessage,
        DomibusConnectorConfirmationType confirmationType, String confirmationAction)
        throws DomibusConnectorClientBackendException;
}
