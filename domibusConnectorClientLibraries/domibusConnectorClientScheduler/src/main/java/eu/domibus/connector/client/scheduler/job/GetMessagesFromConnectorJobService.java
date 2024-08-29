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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

/**
 * This class handles the retrieval of new messages from a connector and delivers them to the client
 * backend. It is responsible for requesting new messages from the connector, handling
 * acknowledgements, and delivering the messages to the appropriate client backend methods.
 */
@Component
@Validated
@Valid
public class GetMessagesFromConnectorJobService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(GetMessagesFromConnectorJobService.class);
    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    @Autowired
    private DomibusConnectorClient connectorClient;
    @Autowired
    GetMessagesFromConnectorJobConfigurationProperties properties;

    /**
     * Requests new messages from the connector and delivers them to the client backend.
     *
     * <p>This method retrieves new messages from the connector using the
     * {@link DomibusConnectorClient#requestNewMessagesFromConnector(Integer, boolean)} method and
     * delivers them to the client backend using the appropriate delivery methods. The method also
     * handles acknowledging messages based on the configuration.
     *
     * @throws DomibusConnectorClientException if there is an error while fetching new messages from
     *                                         the connector
     */
    public void requestNewMessagesFromConnectorAndDeliverThemToClientBackend()
        throws DomibusConnectorClientException {
        var startTime = LocalDateTime.now();
        LOGGER.debug("GetMessagesFromConnectorJob started");

        Map<String, DomibusConnectorMessageType> received =
            connectorClient.requestNewMessagesFromConnector(
                properties.getMaxFetchCount(),
                properties.isAutoAcknowledgeMessages()
            );

        if (received != null && !CollectionUtils.isEmpty(received.values())) {
            LOGGER.info("{} new messages from connector to store...", received.size());
            received.forEach((transportId, message) -> {
                boolean isBusinessMessage = message.getMessageContent() != null;
                boolean isEvidenceMessage =
                    message.getMessageConfirmations() != null && !message.getMessageConfirmations()
                                                                         .isEmpty();
                if (properties.isAutoAcknowledgeMessages()) {
                    if (isBusinessMessage) {
                        try {
                            clientBackend.deliverNewMessageToClientBackend(message);
                        } catch (DomibusConnectorClientBackendException e1) {
                            LOGGER.error(
                                "Exception occured delivering new message to the client backend",
                                e1
                            );
                        }
                    } else if (isEvidenceMessage) {
                        try {
                            clientBackend.deliverNewConfirmationToClientBackend(message);
                        } catch (DomibusConnectorClientBackendException e1) {
                            LOGGER.error(
                                "Exception occured delivering new confirmation to the client "
                                    + "backend",
                                e1
                            );
                        }
                    }
                } else {
                    if (isBusinessMessage) {
                        try {
                            clientBackend.deliverNewAcknowledgeableMessageToClientBackend(
                                message, transportId);
                        } catch (DomibusConnectorClientBackendException e1) {
                            LOGGER.error(
                                "Exception occured delivering new message to the client backend",
                                e1
                            );
                        }
                    } else if (isEvidenceMessage) {
                        try {
                            clientBackend.deliverNewAcknowledgeableConfirmationToClientBackend(
                                message, transportId);
                        } catch (DomibusConnectorClientBackendException e1) {
                            LOGGER.error(
                                "Exception occured delivering new confirmation to the client "
                                    + "backend",
                                e1
                            );
                        }
                    }
                }
            });
        } else {
            LOGGER.debug("No new messages from connector to store received.");
        }
        LOGGER.debug(
            "GetMessagesFromConnectorJob finished after [{}]",
            Duration.between(startTime, LocalDateTime.now())
        );
    }
}
