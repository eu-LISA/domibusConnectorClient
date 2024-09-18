/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.backend.impl;

import eu.ecodex.connector.client.DomibusConnectorClient;
import eu.ecodex.connector.client.DomibusConnectorClientAppBackend;
import eu.ecodex.connector.client.DomibusConnectorClientMessageBuilder;
import eu.ecodex.connector.client.controller.configuration.DefaultConfirmationAction;
import eu.ecodex.connector.client.controller.configuration.DomibusConnectorClientControllerConfig;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.ecodex.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.ecodex.connector.client.controller.rest.impl.DomibusConnectorClientDeliveryRestClient;
import eu.ecodex.connector.client.exception.DomibusConnectorClientBackendException;
import eu.ecodex.connector.client.exception.DomibusConnectorClientException;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessagesType;
import eu.ecodex.connector.domain.transition.tools.ConversionTools;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * The DomibusConnectorClientBackendImpl class represents the implementation of the
 * DomibusConnectorClientBackend interface. It provides functionalities for submitting, delivering,
 * and confirming messages for the Domibus connector client.
 *
 * @see DomibusConnectorClientAppBackend
 */
@Data
@Valid
@Component
@Validated
@SuppressWarnings("squid:S1135")
@NoArgsConstructor
@ConfigurationProperties(prefix = DomibusConnectorClientControllerConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientAppBackend {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientBackendImpl.class);
    @Autowired
    @NotNull
    private DomibusConnectorClientStorage storage;
    @Autowired
    @NotNull
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    @NotNull
    private DomibusConnectorClient connectorClient;
    @Autowired
    @NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;
    @Autowired
    @Nullable
    private DomibusConnectorClientDeliveryRestClient deliveryRestClient;
    @NotNull
    private DefaultConfirmationAction confirmationDefaultAction;

    @Override
    public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() {
        LOGGER.debug("#checkClientForNewMessagesToSubmit: called");

        Map<String, DomibusConnectorMessageType> newMessages = storage.checkStorageForNewMessages();

        if (newMessages != null && !newMessages.isEmpty()) {
            var messages = new DomibusConnectorMessagesType();
            newMessages.keySet().forEach(newMessageLocation -> {
                DomibusConnectorMessageType message = newMessages.get(newMessageLocation);
                PDomibusConnectorClientMessage clientMessage =
                    persistenceService.persistNewMessage(
                        message,
                        PDomibusConnectorClientMessageStatus.SENDING
                    );
                clientMessage.setStorageInfo(newMessageLocation);
                clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);

                try {
                    submitAndUpdateMessage(message, clientMessage);
                } catch (DomibusConnectorClientBackendException
                         | DomibusConnectorClientStorageException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            return messages;
        }

        return null;
    }

    private void submitAndUpdateMessage(
        DomibusConnectorMessageType message, PDomibusConnectorClientMessage clientMessage)
        throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException {
        try {
            connectorClient.submitNewMessageToConnector(message);
            clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.SENT);
        } catch (DomibusConnectorClientException e) {
            clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.FAILED);
            persistenceService.mergeClientMessage(clientMessage);
            throw new DomibusConnectorClientBackendException(
                "Exception submitting message through domibusConnectorClientLibrary!", e);
        }

        try {
            String newStorageLocation =
                storage.updateStoredMessageToSent(clientMessage.getStorageInfo());
            clientMessage.setStorageInfo(newStorageLocation);
        } catch (DomibusConnectorClientStorageException e) {
            throw new DomibusConnectorClientStorageException(
                "Exception updating stored message to \"sent\"!", e);
        }

        persistenceService.mergeClientMessage(clientMessage);
    }

    @Override
    public void submitStoredClientBackendMessage(String storageLocation)
        throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException,
        IllegalArgumentException {
        if (storageLocation == null || storageLocation.isEmpty()) {
            throw new IllegalArgumentException("Storage location is null or empty! ");
        }

        DomibusConnectorMessageType message;
        try {
            message = storage.getStoredMessage(storageLocation);
        } catch (DomibusConnectorClientStorageException e) {
            throw new DomibusConnectorClientStorageException(
                "ClientMessage could not be read from storage! StorageLocation: "
                    + storageLocation);
        }

        if (!StringUtils.hasLength(message.getMessageDetails().getBackendMessageId())) {
            throw new DomibusConnectorClientStorageException(
                "ClientMessage at storageLocation contains no backendMessageId! StorageLocation: "
                    + storageLocation);
        }

        PDomibusConnectorClientMessage clientMessage;
        Optional<PDomibusConnectorClientMessage> finding = persistenceService
            .getMessageDao()
            .findOneByBackendMessageId(message.getMessageDetails().getBackendMessageId());
        if (finding.isPresent()) {
            clientMessage = finding.get();
        } else {
            LOGGER.info(
                "Message in database using backendMessageId {} not found! Persist as "
                    + "new message...",
                message.getMessageDetails().getBackendMessageId()
            );
            clientMessage = this.persistenceService.persistNewMessage(
                message,
                PDomibusConnectorClientMessageStatus.PREPARED
            );
        }

        if (clientMessage.getMessageStatus() == null || !clientMessage.getMessageStatus().equals(
            PDomibusConnectorClientMessageStatus.PREPARED)) {
            throw new DomibusConnectorClientBackendException(
                "ClientMessage in database to submit must have the messageStatus set as PREPARED! "
                    + "Database id: "
                    + clientMessage.getId());
        }

        submitAndUpdateMessage(message, clientMessage);
    }

    @Override
    public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message)
        throws DomibusConnectorClientBackendException {
        LOGGER.debug("#deliverNewMessageToClientBackend: called");
        PDomibusConnectorClientMessage clientMessage = persistenceService.persistNewMessage(
            message,
            PDomibusConnectorClientMessageStatus.RECEIVING
        );
        if (clientMessage != null) {
            clientMessage =
                persistenceService.persistAllConfirmationsForMessage(clientMessage, message);
        }
        LOGGER.debug(
            "#deliverNewMessageToClientBackend: persisted delivered message and its confirmations "
                + "into database"
        );

        String storageLocation;
        try {
            storageLocation = storage.storeMessage(message);
        } catch (DomibusConnectorClientStorageException e) {
            clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.FAILED);
            persistenceService.mergeClientMessage(clientMessage);
            throw new DomibusConnectorClientBackendException(e);
        }

        clientMessage.setStorageInfo(storageLocation);
        clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
        clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.RECEIVED);
        persistenceService.mergeClientMessage(clientMessage);
        LOGGER.debug(
            "#deliverNewMessageToClientBackend: merged delivered message with storageLocation "
                + "and storageStatus into database"
        );

        if (deliveryRestClient != null) {
            LOGGER.info("Delivery Rest Client to Backend is there... message will be delivered!");

            var connectorClientMessage = new DomibusConnectorClientMessage();
            var confirmations = clientMessage.getConfirmations();
            confirmations.forEach(confirmation -> {
                var evidence = new DomibusConnectorClientConfirmation();
                BeanUtils.copyProperties(confirmation, evidence);
                connectorClientMessage.getEvidences().add(evidence);
            });
            BeanUtils.copyProperties(clientMessage, connectorClientMessage);
            connectorClientMessage.setStorageStatus(clientMessage.getStorageStatus().name());
            connectorClientMessage.setMessageStatus(clientMessage.getMessageStatus().name());

            if (filesReadable(clientMessage)) {
                Map<String, DomibusConnectorClientStorageFileType> files = null;
                try {
                    files = storage.listContentAtStorageLocation(clientMessage.getStorageInfo());
                } catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
                    DomibusConnectorClientStorageStatus checkStorageStatus =
                        storage.checkStorageStatus(clientMessage.getStorageInfo());
                    clientMessage.setStorageStatus(checkStorageStatus);
                    persistenceService.mergeClientMessage(clientMessage);
                }
                assert files != null;
                files.forEach((key, value) -> {
                    var fileType =
                        DomibusConnectorClientMessageFileType.valueOf(value.name());
                    var file2 = new DomibusConnectorClientMessageFile(key, fileType);
                    connectorClientMessage.getFiles().add(file2);
                });
            }
            try {
                deliveryRestClient.deliverNewMessageFromConnectorClientToBackend(
                    connectorClientMessage);
                clientMessage.setMessageStatus(
                    PDomibusConnectorClientMessageStatus.DELIVERED_BACKEND);
            } catch (Exception e) {
                LOGGER.error("Delivery to client backend via Rest service failed! ", e);
                clientMessage.setMessageStatus(
                    PDomibusConnectorClientMessageStatus.DELIVERY_FAILED);
            }

            persistenceService.mergeClientMessage(clientMessage);
        }
    }

    @Override
    public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
        throws DomibusConnectorClientBackendException {
        LOGGER.debug("#deliverNewConfirmationToClientBackend: called");
        DomibusConnectorMessageConfirmationType confirmation =
            message.getMessageConfirmations().getFirst();

        PDomibusConnectorClientMessage originalClientMessage =
            persistenceService.findOriginalClientMessage(message);
        if (originalClientMessage == null) {
            throw new DomibusConnectorClientBackendException(
                "Original client message with refToMessageId " + message.getMessageDetails()
                                                                        .getRefToMessageId()
                    + " not found! Confirmation of type " + confirmation.getConfirmationType()
                                                                        .name()
                    + " cannot be stored!");
        }

        String storageLocation = originalClientMessage.getStorageInfo();
        if (storageLocation == null && !originalClientMessage.getStorageStatus().equals(
            DomibusConnectorClientStorageStatus.STORED)) {
            originalClientMessage.setMessageStatus(
                PDomibusConnectorClientMessageStatus.CONFIRMATION_RECEPTION_FAILED);
            persistenceService.mergeClientMessage(originalClientMessage);
            throw new DomibusConnectorClientBackendException(
                "Storage location or status of originalMessage with ebmsId "
                    + message.getMessageDetails().getRefToMessageId()
                    + " not valid! Confirmation of type " + confirmation.getConfirmationType()
                                                                        .name()
                    + " cannot be stored!");
        }

        try {
            storage.storeConfirmationToMessage(message, storageLocation);
        } catch (DomibusConnectorClientStorageException e) {
            originalClientMessage.setMessageStatus(
                PDomibusConnectorClientMessageStatus.CONFIRMATION_RECEPTION_FAILED);
            persistenceService.mergeClientMessage(originalClientMessage);
            throw new DomibusConnectorClientBackendException(e);
        }
        LOGGER.debug("#deliverNewConfirmationToClientBackend: confirmation stored.");

        if (originalClientMessage != null) {
            var newConfirmation =
                persistenceService.persistNewConfirmation(confirmation, originalClientMessage);
            originalClientMessage.getConfirmations().add(newConfirmation);
            originalClientMessage.setLastConfirmationReceived(
                newConfirmation.getConfirmationType()
            );

            switch (confirmation.getConfirmationType()) {
                case RETRIEVAL, DELIVERY:
                    originalClientMessage.setMessageStatus(
                        PDomibusConnectorClientMessageStatus.CONFIRMED);
                    break;
                case NON_DELIVERY, SUBMISSION_REJECTION, RELAY_REMMD_FAILURE, NON_RETRIEVAL,
                     RELAY_REMMD_REJECTION:
                    originalClientMessage.setMessageStatus(
                        PDomibusConnectorClientMessageStatus.REJECTED);
                    break;
                case RELAY_REMMD_ACCEPTANCE, SUBMISSION_ACCEPTANCE:
                    originalClientMessage.setMessageStatus(
                        PDomibusConnectorClientMessageStatus.ACCEPTED);
                    break;
                default:
                    break;
            }

            persistenceService.mergeClientMessage(originalClientMessage);
            LOGGER.debug(
                "#deliverNewConfirmationToClientBackend: confirmation persisted into database and "
                    + "merged with original message."
            );
        }

        if (deliveryRestClient != null) {
            LOGGER.info(
                "Delivery Rest Client to Backend is there... confirmation will be delivered!"
            );

            // Building domibusConnectorClient message with the confirmation and the original
            // backendMessageId
            var clientMessage = new DomibusConnectorClientMessage();
            clientMessage.setBackendMessageId(originalClientMessage.getBackendMessageId());

            var conf = new DomibusConnectorClientConfirmation();
            byte[] confirmationBytes =
                ConversionTools.convertXMLSourceToByteArray(confirmation.getConfirmation());

            conf.setConfirmation(confirmationBytes);
            conf.setConfirmationType(confirmation.getConfirmationType().name());

            clientMessage.getEvidences().add(conf);

            try {
                deliveryRestClient.deliverNewConfirmationFromConnectorClientToBackend(
                    clientMessage);
            } catch (Exception e) {
                LOGGER.error("Delivery to client backend via Rest service failed! ", e);
            }
        }
    }

    @Override
    public void triggerConfirmationForMessage(
        DomibusConnectorMessageType originalMessage,
        DomibusConnectorConfirmationType confirmationType, String confirmationAction)
        throws DomibusConnectorClientBackendException {
        if (confirmationAction == null || confirmationAction.isEmpty()) {
            switch (confirmationType) {
                case DELIVERY, NON_DELIVERY:
                    confirmationAction =
                        confirmationDefaultAction.getDeliveryNonDeliveryToRecipient();
                    break;
                case RETRIEVAL, NON_RETRIEVAL:
                    confirmationAction =
                        confirmationDefaultAction.getRetrievalNonRetrievalToRecipient();
                    break;
                default:
                    throw new DomibusConnectorClientBackendException(
                        "ConfirmationType invalid for connectorClient to trigger! "
                            + confirmationType.name());
            }
        }

        DomibusConnectorMessageType confirmationMessage =
            messageBuilder.createNewConfirmationMessage(
                originalMessage.getMessageDetails().getEbmsMessageId(),
                originalMessage.getMessageDetails().getConversationId(),
                originalMessage.getMessageDetails().getService(),
                confirmationAction,
                originalMessage.getMessageDetails().getToParty(),
                originalMessage.getMessageDetails().getFromParty(),
                originalMessage.getMessageDetails().getOriginalSender(),
                originalMessage.getMessageDetails().getFinalRecipient(),
                confirmationType
            );

        try {
            connectorClient.triggerConfirmationForMessage(confirmationMessage);
        } catch (DomibusConnectorClientException e) {
            throw new DomibusConnectorClientBackendException(e);
        }
    }

    private boolean filesReadable(PDomibusConnectorClientMessage message) {
        return message != null && message.getStorageInfo() != null && !message.getStorageInfo()
                                                                              .isEmpty()
            && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
    }

    @Override
    public void deliverNewAcknowledgeableMessageToClientBackend(
        DomibusConnectorMessageType message,
        String messageTransportId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deliverNewAcknowledgeableConfirmationToClientBackend(
        DomibusConnectorMessageType message,
        String messageTransportId) {
        // TODO Auto-generated method stub
    }
}
