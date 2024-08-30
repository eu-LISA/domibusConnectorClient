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

import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This REST interface allows a backend application that is connected to the domibusConnectorClient
 * to submit messages to it that have not been processed by the domibusConnectorClient before.
 *
 * @author riederb
 */
@RequestMapping(DomibusConnectorClientSubmissionRestAPI.SUBMISSIONRESTSERVICE_PATH)
public interface DomibusConnectorClientSubmissionRestAPI {
    /**
     * This static String is the relative path where this REST service can be reached.
     */
    String SUBMISSIONRESTSERVICE_PATH = "/submissionrestservice";
    /**
     * This static String is the relative path where this method of this REST service can be
     * reached.
     */
    String TRIGGER_CONFIRMATION_AT_CONNECTOR_CLIENT =
        "/triggerConfirmationAtConnectorClient";
    /**
     * This static String is the relative path where this method of this REST service can be
     * reached.
     */
    String SUBMIT_NEW_MESSAGE_FROM_BACKEND_TO_CONNECTOR_CLIENT =
        "/submitNewMessageFromBackendToConnectorClient";

    /**
     * With this method a new message may be submitted to the domibusConnectorClient by a backend
     * application via REST service. The message is then processed by the domibusConnectorClient and
     * submitted to the domibusConnector.
     *
     * @param message The domibusConnectorClient message to be processed and submitted to the
     *                domibusConnector. This message object must already be built completely with
     *                all message files attached, as the domibusConnectorClient presumes that the
     *                message submitted is already prepared.
     * @return success
     * @throws MessageSubmissionException if an error occurs during message submission
     * @throws StorageException           if there is an issue with storage operations
     * @throws ParameterException         if there is an issue with the input parameters
     */
    @PostMapping(
        value = SUBMIT_NEW_MESSAGE_FROM_BACKEND_TO_CONNECTOR_CLIENT, consumes = "application/json",
        produces = "application/json"
    )
    Boolean submitNewMessageFromBackendToConnectorClient(
        @RequestBody DomibusConnectorClientMessage message)
        throws MessageSubmissionException, StorageException, ParameterException;

    /**
     * This method allows a backend application of the domibusConnectorClient to trigger a
     * confirmation for a message. The confirmation trigger will be forwarded to the
     * domibusConnector which then generates the confirmation and submits it to the original sender
     * of the message. The generated confirmation also is sent back to the domibusConnectorClient
     * and stored there.
     *
     * @param refToMessageId   The reference to the message ID for which the confirmation is
     *                         triggered.
     * @param confirmationType The type of confirmation to trigger.
     * @return {@code true} if the confirmation was successfully triggered, {@code false} otherwise.
     * @throws MessageSubmissionException if an error occurs during message submission.
     * @throws ParameterException         if there is an issue with the input parameters.
     * @throws MessageNotFoundException   if the message corresponding to the given reference ID is
     *                                    not found.
     */
    @PostMapping(
        value = TRIGGER_CONFIRMATION_AT_CONNECTOR_CLIENT, consumes = "application/json",
        produces = "application/json"
    )
    Boolean triggerConfirmationAtConnectorClient(
        @RequestParam String refToMessageId, @RequestParam String confirmationType)
        throws MessageSubmissionException, ParameterException, MessageNotFoundException;
}
