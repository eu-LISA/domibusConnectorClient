/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.isupport.writer;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemWriterImpl;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemWriter;
import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.ecodex.connector.client.filesystem.isupport.ISupportFSMessageProperties;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import java.io.File;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * This class is an implementation of the DomibusConnectorClientFileSystemWriter interface for the
 * iSupport profile.
 */
@Component
@Validated
@Valid
@Profile("iSupport")
public class ISupportFSWriterImpl extends AbstractDomibusConnectorClientFileSystemWriterImpl
    implements DomibusConnectorClientFileSystemWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ISupportFSWriterImpl.class);
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;
    @Autowired
    private ISupportFSMessageProperties messageProperties;

    @Override
    protected void putFileToMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType) {
        // nothing to do here since iSupport does not use message properties
    }

    @Override
    protected void removeFileFromMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType) {
        // nothing to do here since iSupport does not use message properties
    }

    @Override
    protected void setMessageSentInMessageProperties(File messageFolder) {
        // nothing to do here since iSupport does not use message properties
    }

    @Override
    public String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir)
        throws DomibusConnectorClientFileSystemException {
        var incomingMessagesDir =
            new File(messagesDir, messageProperties.getISupportIncomingDir());

        if (!incomingMessagesDir.exists()) {
            incomingMessagesDir.mkdir();
            if (!incomingMessagesDir.exists()) {
                LOGGER.error("Problem with directories.");
                return null;
            }
        }

        File messageFolder = createMessageFolder(message, incomingMessagesDir, true);

        LOGGER.debug("Write new message into folder {}", messageFolder.getAbsolutePath());

        var messageContent = message.getMessageContent();
        if (messageContent != null) {
            if (messageContent.getDocument() != null) {
                if (StringUtils.hasText(messageContent.getDocument().getDocumentName())) {
                    messageContent.getDocument().getDocumentName();
                } else {
                    properties.getDefaultPdfFileName();
                }
                var detachedSignature = messageContent.getDocument().getDetachedSignature();
                if (detachedSignature != null && detachedSignature.getDetachedSignature() != null
                    && detachedSignature.getDetachedSignature().length > 0
                    && detachedSignature.getMimeType() != null) {
                    writeDetachedSignatureToFS(messageFolder, detachedSignature);
                }
            }
            if (messageContent.getXmlContent() != null) {
                String fileName = messageProperties.getFileName();
                writeBusinessContentToFS(messageFolder, messageContent, fileName);
            }
        }

        if (message.getMessageAttachments() != null) {
            writeAttachmentsToFS(message, messageFolder);
        }

        if (message.getMessageConfirmations() != null) {
            writeConfirmationsToFS(message, messageFolder);
        }

        return messageFolder.getAbsolutePath();
    }

    @Override
    protected File createMessageFolder(
        DomibusConnectorMessageType message, File messagesDir, boolean createIfNonExistent)
        throws DomibusConnectorClientFileSystemException {
        String documentName = null;
        if (message.getMessageContent() != null && message.getMessageContent().getDocument() != null
            && message.getMessageContent().getDocument().getDocumentName() != null) {
            documentName = message.getMessageContent().getDocument().getDocumentName();
        }

        if (StringUtils.hasText(documentName)) {

            LOGGER.debug(
                "Creating message folder {} in iSupportIncomingMessagesDir {}", documentName,
                messagesDir.getAbsolutePath()
            );

            var messageFolder = new File(messagesDir, documentName);
            if (createIfNonExistent && (!messageFolder.exists() || !messageFolder.isDirectory())) {
                LOGGER.debug(
                    "Message folder {} does not exist. Create folder!",
                    messageFolder.getAbsolutePath()
                );
                if (!messageFolder.mkdir()) {
                    throw new DomibusConnectorClientFileSystemException(
                        "Message folder cannot be created!");
                }
            }

            return messageFolder;
        } else {
            return super.createMessageFolder(message, messagesDir, createIfNonExistent);
        }
    }
}
