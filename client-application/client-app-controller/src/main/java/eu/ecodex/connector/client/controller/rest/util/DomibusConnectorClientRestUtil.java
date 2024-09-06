/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.rest.util;

import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.DomibusConnectorClientMessageBuilder;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.client.storage.exception.DomibusConnectorClientStorageException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The `DomibusConnectorClientRestUtil` class is a utility class that provides methods for mapping
 * and transforming objects related to the Domibus connector client.
 */
@Component
@SuppressWarnings("squid:S1135")
public class DomibusConnectorClientRestUtil {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientRestUtil.class);
    @Autowired
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    private DomibusConnectorClientStorage storage;
    @Autowired
    private DomibusConnectorClientMessageBuilder messageBuilder;

    /**
     * Maps a {@code PDomibusConnectorClientMessage} object to a
     * {@code DomibusConnectorClientMessage} object.
     *
     * @param message          the {@code PDomibusConnectorClientMessage} object to be mapped
     * @param loadFileContents a flag indicating whether to load file contents or not
     * @return the mapped {@code DomibusConnectorClientMessage} object
     */
    public DomibusConnectorClientMessage mapMessageFromModel(
        PDomibusConnectorClientMessage message, boolean loadFileContents) {
        var clientMessage = new DomibusConnectorClientMessage();
        BeanUtils.copyProperties(message, clientMessage);
        clientMessage.setStorageStatus(message.getStorageStatus().name());
        clientMessage.setMessageStatus(message.getMessageStatus().name());

        if (filesReadable(message)) {
            Set<PDomibusConnectorClientConfirmation> confirmations = message.getConfirmations();
            confirmations.forEach(confirmation -> {
                var evidence = new DomibusConnectorClientConfirmation();
                BeanUtils.copyProperties(confirmation, evidence);
                if (loadFileContents) {
                    try {
                        byte[] content =
                            storage.loadFileContentFromStorageLocation(
                                message.getStorageInfo(),
                                confirmation.getConfirmationType()
                                    + ".xml"
                            );
                        evidence.setConfirmation(content);
                    } catch (IllegalArgumentException | DomibusConnectorClientStorageException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                clientMessage.getEvidences().add(evidence);
            });

            Map<String, DomibusConnectorClientStorageFileType> files;
            try {
                files = storage.listContentAtStorageLocation(message.getStorageInfo());
            } catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
                DomibusConnectorClientStorageStatus checkStorageStatus =
                    storage.checkStorageStatus(message.getStorageInfo());
                message.setStorageStatus(checkStorageStatus);
                persistenceService.mergeClientMessage(message);
                return clientMessage;
            }
            files.forEach((key, value) -> {
                var fileType = DomibusConnectorClientMessageFileType.valueOf(value.name());
                var file2 = new DomibusConnectorClientMessageFile(key, fileType);
                file2.setStorageLocation(message.getStorageInfo());
                if (loadFileContents) {
                    try {
                        byte[] fileContent =
                            storage.loadFileContentFromStorageLocation(
                                message.getStorageInfo(),
                                key
                            );
                        file2.setFileContent(fileContent);
                    } catch (IllegalArgumentException | DomibusConnectorClientStorageException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                clientMessage.getFiles().add(file2);
            });
        }

        return clientMessage;
    }

    /**
     * Maps a {@code DomibusConnectorClientMessage} object to a
     * {@code PDomibusConnectorClientMessage} object.
     *
     * @param msg           the {@code DomibusConnectorClientMessage} object to be mapped
     * @param clientMessage the {@code PDomibusConnectorClientMessage} object to map the properties
     *                      to
     * @return the {@code PDomibusConnectorClientMessage} object with properties mapped from the msg
     *      object
     */
    public PDomibusConnectorClientMessage mapMessageToModel(
        DomibusConnectorClientMessage msg, PDomibusConnectorClientMessage clientMessage) {
        if (clientMessage == null) {
            clientMessage = new PDomibusConnectorClientMessage();
        }
        BeanUtils.copyProperties(msg, clientMessage);

        return clientMessage;
    }

    /**
     * Maps a {@code DomibusConnectorClientMessage} object to a {@code DomibusConnectorMessageType}
     * object.
     *
     * @param clientMessage the {@code DomibusConnectorClientMessage} object to be mapped
     * @return the mapped {@code DomibusConnectorMessageType} object
     */
    public DomibusConnectorMessageType mapMessageToTransition(
        DomibusConnectorClientMessage clientMessage) {
        DomibusConnectorMessageType message = messageBuilder.createNewMessage(
            clientMessage.getBackendMessageId(),
            clientMessage.getEbmsMessageId(),
            clientMessage.getConversationId(),
            clientMessage.getService(), clientMessage.getServiceType(),
            clientMessage.getAction(),
            clientMessage.getFromPartyId(), clientMessage.getFromPartyType(),
            clientMessage.getFromPartyRole(),
            clientMessage.getToPartyId(), clientMessage.getToPartyType(),
            clientMessage.getToPartyRole(),
            clientMessage.getFinalRecipient(), clientMessage.getOriginalSender()
        );

        if (clientMessage.getFiles() != null && clientMessage.getFiles().getFiles() != null
            && !clientMessage.getFiles().getFiles().isEmpty()) {
            clientMessage.getFiles().getFiles().forEach(file -> {
                byte[] fileContent = file.getFileContent();
                if (fileContent == null) {
                    try {
                        fileContent = storage.loadFileContentFromStorageLocation(
                            clientMessage.getStorageInfo(), file.getFileName());
                    } catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
                        LOGGER.error(
                            "Exception called storage.loadFileContentFromStorageLocation with "
                                + "storageLocation {} and fileName {}",
                            clientMessage.getStorageInfo(), file.getFileName(), e
                        );
                    }
                }
                if (fileContent != null && fileContent.length > 0) {
                    if (file.getFileType().name().equals(
                        DomibusConnectorClientStorageFileType.BUSINESS_CONTENT.name())) {
                        messageBuilder.addBusinessContentXMLAsBinary(message, fileContent);
                    } else if (file.getFileType().name().equals(
                        DomibusConnectorClientStorageFileType.BUSINESS_DOCUMENT.name())) {
                        messageBuilder.addBusinessDocumentAsBinary(
                            message, fileContent, file.getFileName());
                    } else {
                        messageBuilder.addBusinessAttachmentAsBinaryToMessage(
                            message, file.getFileName(), fileContent, file.getFileName(), null,
                            file.getFileName()
                        );
                    }
                }
            });
        }

        if (clientMessage.getEvidences() != null && !clientMessage.getEvidences().isEmpty()) {
            clientMessage.getEvidences().forEach(confirmation -> {
                var msgConf = new DomibusConnectorMessageConfirmationType();
                msgConf.setConfirmationType(
                    DomibusConnectorConfirmationType.valueOf(confirmation.getConfirmationType()));
                message.getMessageConfirmations().add(msgConf);
            });
        }

        return message;
    }

    /**
     * Maps a list of {@code PDomibusConnectorClientMessage} objects from the model to a list of
     * {@code DomibusConnectorClientMessage} objects.
     *
     * @param findAll          a collection of {@code PDomibusConnectorClientMessage} objects to be
     *                         mapped
     * @param loadFileContents a boolean indicating whether to load file contents or not
     * @return a {@code DomibusConnectorClientMessageList} object containing the mapped messages
     */
    public DomibusConnectorClientMessageList mapMessagesFromModel(
        Iterable<PDomibusConnectorClientMessage> findAll, boolean loadFileContents) {
        var messages = new DomibusConnectorClientMessageList();

        findAll.forEach(message -> {
            DomibusConnectorClientMessage msg = mapMessageFromModel(message, loadFileContents);
            messages.getMessages().add(msg);
        });

        return messages;
    }

    private boolean filesReadable(PDomibusConnectorClientMessage message) {
        return message != null && message.getStorageInfo() != null && !message.getStorageInfo()
                                                                              .isEmpty()
            && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
    }
}
