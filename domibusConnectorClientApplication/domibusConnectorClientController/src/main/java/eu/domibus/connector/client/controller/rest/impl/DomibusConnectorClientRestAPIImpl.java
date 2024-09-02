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
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.util.DomibusConnectorClientRestUtil;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.rest.DomibusConnectorClientRestAPI;
import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the DomibusConnectorClientRestAPI interface for connecting to Domibus connector
 * client. This class handles the REST API requests and provides the necessary functionality to
 * interact with the Domibus connector client.
 *
 * @see DomibusConnectorClientRestAPI
 */
@RestController
@RequestMapping(DomibusConnectorClientRestAPI.RESTSERVICE_PATH)
public class DomibusConnectorClientRestAPIImpl implements DomibusConnectorClientRestAPI {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientRestAPIImpl.class);
    public static final String STORAGE_FAILURE = "Storage failure: ";
    public static final String PARAMETER_FAILURE = "Parameter failure: ";
    @Autowired
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    private DomibusConnectorClientStorage storage;
    @Autowired
    private DomibusConnectorClientAppBackend connectorClientBackend;
    @Autowired
    private DomibusConnectorClientRestUtil util;

    @Override
    public DomibusConnectorClientMessageList getAllMessages() {
        Iterable<PDomibusConnectorClientMessage> findAll =
            persistenceService.getMessageDao().findAll();

        return util.mapMessagesFromModel(findAll, false);
    }

    @Override
    public DomibusConnectorClientMessage getMessageById(Long id) throws MessageNotFoundException {
        Optional<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findById(id);

        if (msg.isPresent()) {
            return util.mapMessageFromModel(msg.get(), false);
        } else {
            throw new MessageNotFoundException("No message with id found in database: " + id);
        }
    }

    @Override
    public DomibusConnectorClientMessage getMessageByBackendMessageId(String backendMessageId)
        throws MessageNotFoundException {
        Optional<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findOneByBackendMessageId(backendMessageId);

        if (msg.isPresent()) {
            return util.mapMessageFromModel(msg.get(), false);
        } else {
            throw new MessageNotFoundException(
                "No message with backendMessageId found in database: " + backendMessageId);
        }
    }

    @Override
    public DomibusConnectorClientMessage getMessageByEbmsMessageId(String ebmsMessageId)
        throws MessageNotFoundException {
        Optional<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findOneByEbmsMessageId(ebmsMessageId);

        if (msg.isPresent()) {
            return util.mapMessageFromModel(msg.get(), false);
        } else {
            throw new MessageNotFoundException(
                "No message with ebmsMessageId found in database: " + ebmsMessageId);
        }
    }

    @Override
    public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId)
        throws MessageNotFoundException {
        List<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findByConversationId(conversationId);

        if (CollectionUtils.isEmpty(msg)) {
            throw new MessageNotFoundException(
                "No messages with conversationId found in database: " + conversationId);
        }

        return util.mapMessagesFromModel(msg, false);
    }

    @Override
    public DomibusConnectorClientMessageList getMessagesByPeriod(Date from, Date to)
        throws MessageNotFoundException {
        List<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findByPeriod(from, to);

        if (CollectionUtils.isEmpty(msg)) {
            throw new MessageNotFoundException("No messages found in database using given period ");
        }

        return util.mapMessagesFromModel(msg, false);
    }

    @Override
    public byte[] loadFileContentFromStorage(String storageLocation, String fileName)
        throws ParameterException {

        byte[] content;
        try {
            content = storage.loadFileContentFromStorageLocation(storageLocation, fileName);
        } catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
            throw new ParameterException(PARAMETER_FAILURE + e.getMessage(), e);
        }
        return content;
    }

    @Override
    public DomibusConnectorClientMessage saveMessage(DomibusConnectorClientMessage message)
        throws ParameterException, StorageException {
        LOGGER.debug("#saveMessage: entered");
        if (StringUtils.isEmpty(message.getBackendMessageId())) {
            String backendMessageId = generateBackendMessageId();
            message.setBackendMessageId(backendMessageId);
        }

        DomibusConnectorMessageType msg = util.mapMessageToTransition(message);

        PDomibusConnectorClientMessage clientMessage;
        if (message.getId() != null) {
            Optional<PDomibusConnectorClientMessage> findById =
                persistenceService.getMessageDao().findById(message.getId());
            if (findById == null || findById.get() == null) {
                clientMessage = persistenceService.persistNewMessage(
                    msg,
                    PDomibusConnectorClientMessageStatus.PREPARED
                );
            } else {
                clientMessage = findById.get();
            }
        } else {
            clientMessage = persistenceService.persistNewMessage(
                msg,
                PDomibusConnectorClientMessageStatus.PREPARED
            );
        }
        message.setCreated(clientMessage.getCreated());
        message.setId(clientMessage.getId());
        clientMessage = util.mapMessageToModel(message, clientMessage);

        String storageLocation;
        try {
            storageLocation = storage.storeMessage(msg);
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException(STORAGE_FAILURE + e.getMessage(), e);
        }
        clientMessage.setStorageInfo(storageLocation);
        clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);

        persistenceService.mergeClientMessage(clientMessage);

        message.setStorageInfo(clientMessage.getStorageInfo());
        message.setStorageStatus(clientMessage.getStorageStatus().name());

        return message;
    }

    @Override
    public Boolean deleteMessageById(Long id) throws ParameterException, StorageException {
        LOGGER.debug("#deleteMessageById: entered with id {}", id);

        Optional<PDomibusConnectorClientMessage> msg =
            persistenceService.getMessageDao().findById(id);

        if (msg.get() != null) {
            var storageInfo = msg.get().getStorageInfo();
            var storageStatus = msg.get().getStorageStatus();
            if (storageInfo != null && !storageInfo.isEmpty()
                && storageStatus != DomibusConnectorClientStorageStatus.DELETED) {
                try {
                    storage.deleteMessageFromStorage(msg.get().getStorageInfo());
                } catch (DomibusConnectorClientStorageException e) {
                    throw new StorageException(STORAGE_FAILURE + e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    throw new ParameterException(PARAMETER_FAILURE + e.getMessage(), e);
                }
            }

            persistenceService.deleteMessage(msg.get());
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean submitStoredClientMessage(DomibusConnectorClientMessage message)
        throws ParameterException, StorageException, MessageSubmissionException {
        LOGGER.debug(
            "#submitStoredClientMessage: entered with storageLocation {}",
            message.getStorageInfo()
        );
        try {
            connectorClientBackend.submitStoredClientBackendMessage(message.getStorageInfo());
        } catch (DomibusConnectorClientBackendException e) {
            throw new MessageSubmissionException("Client backend failure: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(PARAMETER_FAILURE + e.getMessage(), e);
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException(STORAGE_FAILURE + e.getMessage(), e);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean uploadMessageFile(DomibusConnectorClientMessageFile messageFile)
        throws ParameterException, StorageException {
        LOGGER.debug(
            "#uploadMessageFile: entered with fileName {} and storageLocation {}",
            messageFile.getFileName(), messageFile.getStorageLocation()
        );
        try {
            var fileType =
                DomibusConnectorClientStorageFileType.valueOf(messageFile.getFileType().name());
            storage.storeFileIntoStorage(
                messageFile.getStorageLocation(), messageFile.getFileName(), fileType,
                messageFile.getFileContent()
            );
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException(STORAGE_FAILURE + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(PARAMETER_FAILURE + e.getMessage(), e);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteMessageFile(DomibusConnectorClientMessageFile messageFile)
        throws ParameterException, StorageException {
        LOGGER.debug(
            "#deleteMessageFile: entered with fileName {} and storageLocation {}",
            messageFile.getFileName(), messageFile.getStorageLocation()
        );
        try {
            var fileType =
                DomibusConnectorClientStorageFileType.valueOf(messageFile.getFileType().name());
            storage.deleteFileFromStorage(
                messageFile.getStorageLocation(), messageFile.getFileName(), fileType);
        } catch (DomibusConnectorClientStorageException e) {
            throw new StorageException(STORAGE_FAILURE + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(PARAMETER_FAILURE + e.getMessage(), e);
        }
        return Boolean.TRUE;
    }

    private String generateBackendMessageId() {
        return UUID.randomUUID() + "@connector-client.eu";
    }
}
