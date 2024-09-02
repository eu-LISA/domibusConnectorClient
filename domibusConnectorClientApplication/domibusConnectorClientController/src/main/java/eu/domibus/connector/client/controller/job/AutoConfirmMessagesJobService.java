/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.job;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * The AutoConfirmMessagesJobService class is responsible for automatically confirming messages by
 * triggering confirmation for each unconfirmed message. It retrieves a list of unconfirmed messages
 * using the persistence service and iterates through each message to trigger the appropriate
 * confirmation based on the message's storage status.
 */
@Component
@Validated
@Valid
public class AutoConfirmMessagesJobService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AutoConfirmMessagesJobService.class);
    @Autowired
    @NotNull
    private DomibusConnectorClientBackend clientBackend;
    @Autowired
    @NotNull
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    @NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;

    /**
     * Automatically confirms messages by triggering confirmation for each unconfirmed message. It
     * retrieves a list of unconfirmed messages using the persistence service and iterates through
     * each message.
     *
     * @see AutoConfirmMessagesJobService
     * @see PDomibusConnectorClientMessage
     * @see DomibusConnectorConfirmationType
     * @see DomibusConnectorMessageType
     * @see DomibusConnectorClientBackendException
     * @see PDomibusConnectorClientMessageStatus
     * @see DomibusConnectorClientStorageStatus
     */
    public void autoConfirmMessages() {
        var startTime = LocalDateTime.now();
        LOGGER.debug("AutoConfirmMessagesJobService started");

        List<PDomibusConnectorClientMessage> unconfirmedMessages =
            persistenceService.getMessageDao().findReceived();

        unconfirmedMessages.forEach(message -> {
            DomibusConnectorConfirmationType type;
            if (message.getStorageInfo() != null && !message.getStorageInfo().isEmpty()
                && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED)) {
                type = DomibusConnectorConfirmationType.DELIVERY;
            } else {
                type = DomibusConnectorConfirmationType.NON_DELIVERY;
            }
            DomibusConnectorMessageType originalMessage = messageBuilder.createNewMessage(
                message.getBackendMessageId(),
                message.getEbmsMessageId(),
                message.getConversationId(),
                message.getService(),
                message.getServiceType(),
                message.getAction(),
                message.getFromPartyId(),
                message.getFromPartyType(),
                message.getFromPartyRole(),
                message.getToPartyId(),
                message.getToPartyType(),
                message.getToPartyRole(),
                message.getFinalRecipient(),
                message.getOriginalSender()
            );
            try {
                clientBackend.triggerConfirmationForMessage(originalMessage, type, null);
            } catch (DomibusConnectorClientBackendException e) {
                LOGGER.error(
                    "Exception occured triggering the confirmation for message at the client "
                        + "backend",
                    e
                );
            }
            message.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_TRIGGERED);
            persistenceService.mergeClientMessage(message);
        });

        LOGGER.debug(
            "AutoConfirmMessagesJobService finished after [{}]",
            Duration.between(startTime, LocalDateTime.now())
        );
    }
}
