/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.persistence.service;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.client.controller.persistence.dao.PDomibusConnectorClientConfirmationDao;
import eu.ecodex.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import java.util.Date;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The {@code DomibusConnectorClientPersistenceService} class is responsible for performing
 * persistence operations related to Domibus connector client messages and confirmations.
 */
@Component
@NoArgsConstructor
public class DomibusConnectorClientPersistenceService
    implements IDomibusConnectorClientPersistenceService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientPersistenceService.class);
    public static final String
        FOUND_BY_BACKEND_MESSAGE_ID =
        "#findOriginalClientMessage: original message found by backendMessageId";
    @Autowired
    private PDomibusConnectorClientMessageDao messageDao;
    @Autowired
    private PDomibusConnectorClientConfirmationDao confirmationDao;

    @Override
    public PDomibusConnectorClientMessage persistNewMessage(
        DomibusConnectorMessageType message, PDomibusConnectorClientMessageStatus status) {
        if (message != null) {
            var newMessage = new PDomibusConnectorClientMessage();
            DomibusConnectorMessageDetailsType messageDetails = message.getMessageDetails();
            if (messageDetails != null) {
                newMessage.setBackendMessageId(messageDetails.getBackendMessageId());
                newMessage.setConversationId(messageDetails.getConversationId());
                newMessage.setEbmsMessageId(messageDetails.getEbmsMessageId());
                newMessage.setFinalRecipient(messageDetails.getFinalRecipient());
                newMessage.setOriginalSender(messageDetails.getOriginalSender());
                if (messageDetails.getAction() != null) {
                    newMessage.setAction(messageDetails.getAction().getAction());
                }
                if (messageDetails.getService() != null) {
                    newMessage.setService(messageDetails.getService().getService());
                    newMessage.setServiceType(messageDetails.getService().getServiceType());
                }

                DomibusConnectorPartyType fromParty = messageDetails.getFromParty();
                if (fromParty != null) {
                    newMessage.setFromPartyId(fromParty.getPartyId());
                    newMessage.setFromPartyRole(fromParty.getRole());
                    newMessage.setFromPartyType(fromParty.getPartyIdType());
                }

                DomibusConnectorPartyType toParty = messageDetails.getToParty();
                if (toParty != null) {
                    newMessage.setToPartyId(toParty.getPartyId());
                    newMessage.setToPartyRole(toParty.getRole());
                    newMessage.setToPartyType(toParty.getPartyIdType());
                }
            }
            newMessage.setCreated(new Date());
            newMessage.setMessageStatus(status);

            newMessage = messageDao.save(newMessage);

            return newMessage;
        }
        return null;
    }

    @Override
    public PDomibusConnectorClientMessage persistAllConfirmationsForMessage(
        PDomibusConnectorClientMessage clientMessage, DomibusConnectorMessageType message) {
        var messageConfirmations = message.getMessageConfirmations();
        if (messageConfirmations != null && !messageConfirmations.isEmpty()) {
            messageConfirmations.forEach(messageConfirmation -> {
                var clientConfirmation = persistNewConfirmation(messageConfirmation, clientMessage);
                clientMessage.getConfirmations().add(clientConfirmation);
                clientMessage.setLastConfirmationReceived(clientConfirmation.getConfirmationType());
            });
            messageDao.save(clientMessage);
        }
        return clientMessage;
    }

    @Override
    public PDomibusConnectorClientConfirmation persistNewConfirmation(
        DomibusConnectorMessageConfirmationType confirmation,
        PDomibusConnectorClientMessage clientMessage) {
        var clientConfirmation = new PDomibusConnectorClientConfirmation();
        clientConfirmation.setMessage(clientMessage);
        clientConfirmation.setConfirmationType(confirmation.getConfirmationType().name());
        clientConfirmation.setReceived(new Date());

        clientConfirmation = confirmationDao.save(clientConfirmation);

        return clientConfirmation;
    }

    @Override
    public PDomibusConnectorClientMessage mergeClientMessage(
        PDomibusConnectorClientMessage clientMessage) {

        confirmationDao.saveAll(clientMessage.getConfirmations());
        clientMessage = messageDao.save(clientMessage);

        return clientMessage;
    }

    @Override
    public PDomibusConnectorClientMessage findOriginalClientMessage(
        DomibusConnectorMessageType message) {
        var messageDetails = message.getMessageDetails();
        if (messageDetails != null) {

            if (messageDetails.getRefToMessageId() != null) {
                LOGGER.debug(
                    "#findOriginalClientMessage: try with refToMessageId {}",
                    messageDetails.getRefToMessageId()
                );
                Optional<PDomibusConnectorClientMessage> clientMessage =
                    messageDao.findOneByEbmsMessageId(messageDetails.getRefToMessageId());
                if (clientMessage.isPresent()) {
                    LOGGER.debug("#findOriginalClientMessage: original message found by ebmsId");
                    return clientMessage.get();
                } else {
                    clientMessage =
                        messageDao.findOneByBackendMessageId(messageDetails.getRefToMessageId());
                    if (clientMessage.isPresent()) {
                        LOGGER.debug(FOUND_BY_BACKEND_MESSAGE_ID);
                        return clientMessage.get();
                    }
                }
            }

            if (messageDetails.getBackendMessageId() != null) {
                LOGGER.debug(
                    "#findOriginalClientMessage: try with backendMessageId {}",
                    messageDetails.getBackendMessageId()
                );
                Optional<PDomibusConnectorClientMessage> clientMessage =
                    messageDao.findOneByBackendMessageId(messageDetails.getBackendMessageId());
                if (clientMessage.isPresent()) {
                    LOGGER.debug(FOUND_BY_BACKEND_MESSAGE_ID);
                    return clientMessage.get();
                }
            }
        }

        return null;
    }

    @Override
    public PDomibusConnectorClientMessageDao getMessageDao() {
        return messageDao;
    }

    @Override
    public void deleteMessage(PDomibusConnectorClientMessage clientMessage) {
        LOGGER.debug("#deleteMessage: called");
        if (!clientMessage.getConfirmations().isEmpty()) {
            this.confirmationDao.deleteAll(clientMessage.getConfirmations());
        }

        this.messageDao.delete(clientMessage);
        LOGGER.debug("#deleteMessage: success");
    }
}
