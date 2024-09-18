/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.database;

import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.ecodex.connector.client.controller.persistence.service.DomibusConnectorClientPersistenceService;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * The DatabaseRestore class is responsible for restoring messages from a storage to a database. It
 * provides methods for restoring the entire database from the storage, as well as restoring a
 * single message.
 */
@Data
@NoArgsConstructor
public class DatabaseRestore {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRestore.class);
    private DomibusConnectorClientStorage storage;
    private DomibusConnectorClientPersistenceService persistenceService;

    /**
     * Restores the database from the storage. This method retrieves all messages stored in the
     * storage and restores them to the database if they are not already present. It uses the
     * configured storage and persistence service to perform the restore operation.
     */
    public void restoreDatabaseFromStorage() {
        LOGGER.debug("#restoreDatabaseFromStorage: enter");

        if (getStorage() != null) {
            LOGGER.debug("Storage set!");
        }

        if (getPersistenceService() != null) {
            LOGGER.debug("PersistenceService set!");
        }

        Map<String, DomibusConnectorMessageType> allStoredMessages = storage.getAllStoredMessages();

        if (allStoredMessages != null && !allStoredMessages.isEmpty()) {
            LOGGER.info("Found {} messages in storage to restore.", allStoredMessages.size());
            allStoredMessages.keySet().forEach(storageLocation -> {
                DomibusConnectorMessageType message = allStoredMessages.get(storageLocation);

                restoreMessageToDatabase(storageLocation, message);
            });
        }
    }

    /**
     * Restores a message to the database from a storage location.
     *
     * @param storageLocation the location of the message in the storage.
     * @param message         the message to be restored.
     */
    public void restoreMessageToDatabase(
        String storageLocation, DomibusConnectorMessageType message) {
        LOGGER.debug("#restoreDatabaseFromStorage: enter");

        if (message == null || message.getMessageDetails() == null) {
            LOGGER.warn(
                "Message from storageLocation {} cannot be restored as no message details are "
                    + "given!",
                storageLocation
            );
        }

        var ebmsId = message.getMessageDetails().getEbmsMessageId();
        var backendId = message.getMessageDetails().getBackendMessageId();

        if (StringUtils.hasLength(ebmsId) && StringUtils.hasLength(backendId)) {
            var finding = persistenceService.getMessageDao()
                                            .findOneByEbmsMessageIdAndBackendMessageId(
                                                ebmsId,
                                                backendId
                                            );
            if (finding.isPresent()) {
                LOGGER.info(
                    "Found message with ebmsId {} and backendId {} in database.", ebmsId,
                    backendId
                );
                PDomibusConnectorClientMessage preparedDatabaseEntry =
                    prepareDatabaseEntry(finding.get(), message, storageLocation,
                                         PDomibusConnectorClientMessageStatus.RECEIVED
                    );
                this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
                return;
            }
        }

        if (StringUtils.hasLength(ebmsId)) {
            var finding = persistenceService.getMessageDao().findOneByEbmsMessageId(ebmsId);
            if (finding.isPresent()) {
                PDomibusConnectorClientMessage possibleFinding = finding.get();
                if (possibleFinding.getStorageInfo().equals(storageLocation)) {
                    LOGGER.info(
                        "Found message with ebmsId {} and storageLocation {} in database.", ebmsId,
                        storageLocation
                    );
                    PDomibusConnectorClientMessage preparedDatabaseEntry =
                        prepareDatabaseEntry(possibleFinding, message, storageLocation,
                                             PDomibusConnectorClientMessageStatus.RECEIVED
                        );
                    this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
                    return;
                }
            }
        }

        if (StringUtils.hasLength(backendId)) {
            var finding = persistenceService.getMessageDao().findOneByBackendMessageId(backendId);
            if (finding.isPresent()) {
                PDomibusConnectorClientMessage possibleFinding = finding.get();
                if (possibleFinding.getStorageInfo().equals(storageLocation)) {
                    LOGGER.info(
                        "Found message with backendId {} and storageLocation {} in database.",
                        backendId, storageLocation
                    );
                    PDomibusConnectorClientMessage preparedDatabaseEntry =
                        prepareDatabaseEntry(possibleFinding, message, storageLocation,
                                             PDomibusConnectorClientMessageStatus.PREPARED
                        );
                    this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
                    return;
                }
            }
        }

        LOGGER.info(
            "Message from storageLocation {} not in database yet. Creating one!", storageLocation);

        var preparedDatabaseEntry = prepareDatabaseEntry(
            new PDomibusConnectorClientMessage(),
            message,
            storageLocation,
            ebmsId != null
                ? PDomibusConnectorClientMessageStatus.RECEIVED
                : PDomibusConnectorClientMessageStatus.PREPARED
        );

        this.persistenceService.getMessageDao().save(preparedDatabaseEntry);

        preparedDatabaseEntry = handleConfirmations(preparedDatabaseEntry, message);

        this.persistenceService.getMessageDao().save(preparedDatabaseEntry);
    }

    private PDomibusConnectorClientMessage prepareDatabaseEntry(
        PDomibusConnectorClientMessage clientMessage, DomibusConnectorMessageType message,
        String storageLocation, PDomibusConnectorClientMessageStatus messageStatus) {
        clientMessage.setAction(message.getMessageDetails().getAction() != null
                                    ? message.getMessageDetails().getAction().getAction()
                                    : null
        );
        clientMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        clientMessage.setConversationId(message.getMessageDetails().getConversationId());
        clientMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        clientMessage.setFinalRecipient(message.getMessageDetails().getFinalRecipient());
        if (message.getMessageDetails().getFromParty() != null) {
            clientMessage.setFromPartyId(message.getMessageDetails().getFromParty().getPartyId());
            clientMessage.setFromPartyRole(message.getMessageDetails().getFromParty().getRole());
            clientMessage.setFromPartyType(message.getMessageDetails().getFromParty()
                                                  .getPartyIdType());
        }
        clientMessage.setOriginalSender(message.getMessageDetails().getOriginalSender());
        clientMessage.setService(message.getMessageDetails().getService() != null
                                     ? message.getMessageDetails().getService().getService()
                                     : null
        );
        clientMessage.setServiceType(message.getMessageDetails().getService() != null
                                         ? message.getMessageDetails().getService().getServiceType()
                                         : null
        );
        clientMessage.setStorageInfo(storageLocation);
        clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
        if (message.getMessageDetails().getToParty() != null) {
            clientMessage.setToPartyId(message.getMessageDetails().getToParty().getPartyId());
            clientMessage.setToPartyRole(message.getMessageDetails().getToParty().getRole());
            clientMessage.setToPartyType(message.getMessageDetails().getToParty().getPartyIdType());
        }
        clientMessage.setMessageStatus(messageStatus);
        if (clientMessage.getCreated() == null) {
            clientMessage.setCreated(new Date());
        }

        return clientMessage;
    }

    private PDomibusConnectorClientMessage handleConfirmations(
        PDomibusConnectorClientMessage clientMessage, DomibusConnectorMessageType message) {
        if (!CollectionUtils.isEmpty(message.getMessageConfirmations())) {
            List<DomibusConnectorMessageConfirmationType> messageConfirmations = new ArrayList<>(
                message.getMessageConfirmations());
            if (!CollectionUtils.isEmpty(clientMessage.getConfirmations())) {
                clientMessage.getConfirmations().forEach(confirmation -> {
                    String type = confirmation.getConfirmationType();

                    message.getMessageConfirmations().forEach(msgConf -> {
                        if (msgConf.getConfirmationType().name().equals(type)) {
                            clientMessage.setMessageStatus(
                                confirmationTypeToMessageStatus(
                                    msgConf.getConfirmationType(),
                                    clientMessage.getMessageStatus(),
                                    message.getMessageDetails()
                                           .getEbmsMessageId()
                                ));
                            messageConfirmations.remove(msgConf);
                        }
                    });
                });
            }

            if (!messageConfirmations.isEmpty()) {
                messageConfirmations.forEach(conf -> {
                    PDomibusConnectorClientConfirmation clientConf =
                        this.persistenceService.persistNewConfirmation(conf, clientMessage);
                    clientMessage.getConfirmations().add(clientConf);
                    clientMessage.setMessageStatus(
                        confirmationTypeToMessageStatus(
                            conf.getConfirmationType(),
                            clientMessage.getMessageStatus(),
                            message.getMessageDetails()
                                   .getEbmsMessageId()
                        ));
                });
            }
        }

        return clientMessage;
    }

    private PDomibusConnectorClientMessageStatus confirmationTypeToMessageStatus(
        DomibusConnectorConfirmationType type, PDomibusConnectorClientMessageStatus messageStatus,
        String ebmsId) {
        switch (type) {
            case RETRIEVAL, DELIVERY:
                return PDomibusConnectorClientMessageStatus.CONFIRMED;
            case NON_DELIVERY, SUBMISSION_REJECTION, RELAY_REMMD_FAILURE, NON_RETRIEVAL,
                 RELAY_REMMD_REJECTION:
                return PDomibusConnectorClientMessageStatus.REJECTED;
            case RELAY_REMMD_ACCEPTANCE, SUBMISSION_ACCEPTANCE:
                if (!messageStatus.equals(PDomibusConnectorClientMessageStatus.CONFIRMED)) {
                    if (StringUtils.hasLength(ebmsId)) {
                        return PDomibusConnectorClientMessageStatus.RECEIVED;
                    } else {
                        return PDomibusConnectorClientMessageStatus.ACCEPTED;
                    }
                }
                break;
            default:
                return null;
        }
        return messageStatus;
    }
}
