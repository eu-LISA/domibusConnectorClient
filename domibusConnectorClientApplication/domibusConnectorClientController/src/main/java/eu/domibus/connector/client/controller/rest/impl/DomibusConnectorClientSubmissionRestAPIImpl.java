/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.rest.impl;

import eu.domibus.connector.client.DomibusConnectorClientAppBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.util.DomibusConnectorClientRestUtil;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.rest.DomibusConnectorClientSubmissionRestAPI;
import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the {@link DomibusConnectorClientSubmissionRestAPI} interface that provides
 * REST endpoints for submitting messages and triggering confirmations in the Domibus Connector
 * Client.
 */
@RestController
@RequestMapping(DomibusConnectorClientSubmissionRestAPI.SUBMISSIONRESTSERVICE_PATH)
public class DomibusConnectorClientSubmissionRestAPIImpl
    implements DomibusConnectorClientSubmissionRestAPI {
    @Autowired
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    private DomibusConnectorClientStorage storage;
    @Autowired
    private DomibusConnectorClientAppBackend connectorClientBackend;
    @Autowired
    @NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;
    @Autowired
    private DomibusConnectorClientRestUtil util;

    @Override
    public Boolean submitNewMessageFromBackendToConnectorClient(
        DomibusConnectorClientMessage clientMessage)
        throws MessageSubmissionException, StorageException, ParameterException {

        DomibusConnectorMessageType message = util.mapMessageToTransition(clientMessage);

        // persist the message into the connector client database...
        PDomibusConnectorClientMessage connectorClientMessage =
            persistenceService.persistNewMessage(
                message,
                PDomibusConnectorClientMessageStatus.PREPARED
            );

        // store the message into storage...
        String storageLocation;
        try {
            storageLocation = storage.storeMessage(message);
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException("Storage failure: " + e.getMessage(), e);
        }

        // update database object with storage info and merge into database...
        connectorClientMessage.setStorageInfo(storageLocation);
        connectorClientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);

        persistenceService.mergeClientMessage(connectorClientMessage);

        // submit the message to connector.
        try {
            connectorClientBackend.submitStoredClientBackendMessage(
                connectorClientMessage.getStorageInfo());
        } catch (DomibusConnectorClientBackendException e) {
            throw new MessageSubmissionException("Client backend failure: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ParameterException("Parameter failure: " + e.getMessage(), e);
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException("Storage failure: " + e.getMessage(), e);
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean triggerConfirmationAtConnectorClient(
        String refToMessageId, String confirmationType)
        throws MessageSubmissionException, MessageNotFoundException {

        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(new DomibusConnectorMessageDetailsType());
        message.getMessageDetails().setRefToMessageId(refToMessageId);
        // find original message...
        PDomibusConnectorClientMessage originalClientMessage =
            persistenceService.findOriginalClientMessage(message);

        if (originalClientMessage == null) {
            throw new MessageNotFoundException(
                "Original client message with refToMessageId " + message.getMessageDetails()
                                                                        .getRefToMessageId()
                    + " not found!");
        }

        DomibusConnectorMessageType originalMessage = messageBuilder.createNewMessage(
            originalClientMessage.getBackendMessageId(),
            originalClientMessage.getEbmsMessageId(),
            originalClientMessage.getConversationId(),
            originalClientMessage.getService(),
            originalClientMessage.getServiceType(),
            originalClientMessage.getAction(),
            originalClientMessage.getFromPartyId(),
            originalClientMessage.getFromPartyType(),
            originalClientMessage.getFromPartyRole(),
            originalClientMessage.getToPartyId(),
            originalClientMessage.getToPartyType(),
            originalClientMessage.getToPartyRole(),
            originalClientMessage.getFinalRecipient(),
            originalClientMessage.getOriginalSender()
        );

        // extract confirmation type
        var connectorConfirmationType = DomibusConnectorConfirmationType.valueOf(confirmationType);

        try {
            connectorClientBackend.triggerConfirmationForMessage(
                originalMessage, connectorConfirmationType, null
            );
        } catch (DomibusConnectorClientBackendException e) {
            throw new MessageSubmissionException("Client backend failure: " + e.getMessage(), e);
        }
        return null;
    }
}
