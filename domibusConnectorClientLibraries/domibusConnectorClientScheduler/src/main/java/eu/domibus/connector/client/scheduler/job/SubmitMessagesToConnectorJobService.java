/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * This class is responsible for submitting new messages from the client's backend to the connector.
 * It checks the client's backend for new messages, retrieves them, and submits them to the
 * connector.
 */
@Component
@Validated
@Valid
public class SubmitMessagesToConnectorJobService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SubmitMessagesToConnectorJobService.class);
    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    @Autowired
    private DomibusConnectorClient connectorClient;

    /**
     * This method is responsible for checking the client's backend for new messages and submitting
     * them to the connector. It retrieves any new messages from the backend and submits them one by
     * one to the connector using the connectorClient instance.
     *
     * @throws DomibusConnectorClientBackendException if there is an error in the backend operations
     *                                                of the client while checking for new messages
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    public void checkClientBackendForNewMessagesAndSubmitThemToConnector() {
        var startTime = LocalDateTime.now();
        LOGGER.debug("SubmitMessagesToConnectorJob started");

        DomibusConnectorMessagesType newMessages = null;
        try {
            newMessages = clientBackend.checkClientForNewMessagesToSubmit();
        } catch (DomibusConnectorClientBackendException e1) {
            LOGGER.error(
                "Exception occured at clientBackend.checkClientForNewMessagesToSubmit()",
                e1
            );
            e1.printStackTrace();
        }

        if (newMessages != null && newMessages.getMessages() != null
            && !newMessages.getMessages().isEmpty()) {
            LOGGER.info(
                "Found {} new message on the client's backend side to submit.",
                newMessages.getMessages().size()
            );
            for (DomibusConnectorMessageType newMessage : newMessages.getMessages()) {
                try {
                    connectorClient.submitNewMessageToConnector(newMessage);
                } catch (DomibusConnectorClientException e) {
                    LOGGER.error("Exception occured submitting new message to domibusConnector!");
                }
            }
        }

        LOGGER.debug(
            "SubmitMessagesToConnectorJob finished after [{}]",
            Duration.between(startTime, LocalDateTime.now())
        );
    }
}
