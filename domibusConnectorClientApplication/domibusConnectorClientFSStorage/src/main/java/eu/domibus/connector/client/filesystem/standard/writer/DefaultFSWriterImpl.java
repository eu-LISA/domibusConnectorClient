/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem.standard.writer;

import eu.domibus.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemWriterImpl;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemUtil;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.filesystem.standard.DefaultMessageProperties;
import eu.domibus.connector.client.filesystem.standard.DomibusConnectorClientFSMessageProperties;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Represents an implementation of the {@link DomibusConnectorClientFileSystemWriter} interface.
 * This class is responsible for writing messages to the file system.
 */
public class DefaultFSWriterImpl extends AbstractDomibusConnectorClientFileSystemWriterImpl
    implements DomibusConnectorClientFileSystemWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFSWriterImpl.class);
    @Autowired
    private DomibusConnectorClientFSMessageProperties messageProperties;
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;

    private void storeMessagePropertiesToFile(
        DefaultMessageProperties messageProperties,
        File messagePropertiesFile) {
        if (!messagePropertiesFile.exists()) {
            try {
                messagePropertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messageProperties.storeMessagePropertiesToFile(messagePropertiesFile);
    }

    /**
     * Writes a message to the file system.
     *
     * @param message     The DomibusConnectorMessageType object representing the message to be
     *                    written.
     * @param messagesDir The directory where the messages are stored.
     * @return The absolute path of the created message folder.
     * @throws DomibusConnectorClientFileSystemException if an error occurs while writing the
     *                                                   message to the file system.
     */
    public String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir)
        throws DomibusConnectorClientFileSystemException {
        File messageFolder = createMessageFolder(message, messagesDir, true);

        LOGGER.debug("Write new message into folder {}", messageFolder.getAbsolutePath());

        var messageReceived = new Date();

        DefaultMessageProperties msgProps = null;
        File messagePropertiesFile = null;
        String action = null;
        if (message.getMessageDetails() != null) {
            if (message.getMessageDetails().getAction() != null) {
                action = message.getMessageDetails().getAction().getAction();
            }
            String messagePropertiesPath =
                messageFolder.getAbsolutePath() + File.separator + messageProperties.getFileName();

            messagePropertiesFile = new File(messagePropertiesPath);
            msgProps = convertMessageDetailsToMessageProperties(
                message
                    .getMessageDetails(), messageReceived);
        }

        DomibusConnectorMessageContentType messageContent = message.getMessageContent();
        if (messageContent != null) {
            if (messageContent.getDocument() != null) {
                String fileName;
                if (StringUtils.hasText(messageContent.getDocument().getDocumentName())) {
                    fileName = messageContent.getDocument().getDocumentName();
                } else {
                    fileName = action != null ? action + "." + properties.getPdfFileExtension()
                        : properties.getDefaultPdfFileName();
                }
                writeBusinessDocumentToFS(messageFolder, fileName, messageContent);
                msgProps.getMessageProperties()
                        .put(messageProperties.getContentPdfFileName(), fileName);
                DomibusConnectorDetachedSignatureType detachedSignature =
                    messageContent.getDocument().getDetachedSignature();
                if (detachedSignature != null && detachedSignature.getDetachedSignature() != null
                    && detachedSignature.getDetachedSignature().length > 0
                    && detachedSignature.getMimeType() != null) {
                    String fileName2 = writeDetachedSignatureToFS(messageFolder, detachedSignature);
                    msgProps.getMessageProperties()
                            .put(messageProperties.getDetachedSignatureFileName(), fileName2);
                }
            }
            if (messageContent.getXmlContent() != null) {
                String fileName = action != null ? action + properties.getXmlFileExtension()
                    : properties.getDefaultXmlFileName();
                writeBusinessContentToFS(messageFolder, messageContent, fileName);
                msgProps.getMessageProperties()
                        .put(messageProperties.getContentXmlFileName(), fileName);
            }
        }
        LOGGER.debug(
            "Store message properties to file {}", messagePropertiesFile.getAbsolutePath());
        storeMessagePropertiesToFile(msgProps, messagePropertiesFile);

        if (message.getMessageAttachments() != null) {
            writeAttachmentsToFS(message, messageFolder);
        }

        if (message.getMessageConfirmations() != null) {
            writeConfirmationsToFS(message, messageFolder);
        }

        return messageFolder.getAbsolutePath();
    }

    private DefaultMessageProperties convertMessageDetailsToMessageProperties(
        DomibusConnectorMessageDetailsType messageDetails, Date messageReceived) {

        var msgDetails = new DefaultMessageProperties();
        if (StringUtils.hasText(messageDetails.getEbmsMessageId())) {
            msgDetails.getMessageProperties()
                      .put(messageProperties.getEbmsMessageId(), messageDetails.getEbmsMessageId());
        }
        if (StringUtils.hasText(messageDetails.getBackendMessageId())) {
            msgDetails.getMessageProperties().put(
                messageProperties.getBackendMessageId(),
                messageDetails.getBackendMessageId()
            );
        }
        if (StringUtils.hasText(messageDetails.getConversationId())) {
            msgDetails.getMessageProperties().put(
                messageProperties.getConversationId(),
                messageDetails.getConversationId()
            );
        }
        if (messageDetails.getToParty() != null) {
            msgDetails.getMessageProperties().put(
                messageProperties.getToPartyId(),
                messageDetails.getToParty().getPartyId()
            );
            msgDetails.getMessageProperties().put(
                messageProperties.getToPartyRole(),
                messageDetails.getToParty().getRole()
            );
        }
        if (messageDetails.getFromParty() != null) {
            msgDetails.getMessageProperties().put(
                messageProperties.getFromPartyId(),
                messageDetails.getFromParty().getPartyId()
            );
            msgDetails.getMessageProperties().put(
                messageProperties.getFromPartyRole(),
                messageDetails.getFromParty().getRole()
            );
        }
        msgDetails.getMessageProperties()
                  .put(messageProperties.getFinalRecipient(), messageDetails.getFinalRecipient());
        msgDetails.getMessageProperties()
                  .put(messageProperties.getOriginalSender(), messageDetails.getOriginalSender());
        if (messageDetails.getAction() != null) {
            msgDetails.getMessageProperties()
                      .put(messageProperties.getAction(), messageDetails.getAction().getAction());
        }
        if (messageDetails.getService() != null) {
            msgDetails.getMessageProperties().put(
                messageProperties.getService(),
                messageDetails.getService().getService()
            );
            msgDetails.getMessageProperties().put(
                messageProperties.getServiceType(),
                messageDetails.getService().getServiceType()
            );
        }
        if (messageReceived != null) {
            msgDetails.getMessageProperties().put(
                messageProperties.getMessageReceivedDatetime(),
                DomibusConnectorClientFileSystemUtil.convertDateToProperty(
                    messageReceived)
            );
        }

        return msgDetails;
    }

    protected void putFileToMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType) {
        var messageDetails =
            new DefaultMessageProperties(messageFolder, this.messageProperties.getFileName());

        switch (fileType) {
            case BUSINESS_CONTENT:
                messageDetails.getMessageProperties()
                              .put(messageProperties.getContentXmlFileName(), fileName);
                break;
            case BUSINESS_DOCUMENT:
                messageDetails.getMessageProperties()
                              .put(messageProperties.getContentPdfFileName(), fileName);
                break;
            case DETACHED_SIGNATURE:
                messageDetails.getMessageProperties()
                              .put(messageProperties.getDetachedSignatureFileName(), fileName);
                break;
            default:
        }

        var messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
        storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
    }

    protected void removeFileFromMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType) {
        var messageDetails =
            new DefaultMessageProperties(messageFolder, this.messageProperties.getFileName());

        switch (fileType) {
            case BUSINESS_CONTENT:
                messageDetails.getMessageProperties()
                              .remove(messageProperties.getContentXmlFileName(), fileName);
                break;
            case BUSINESS_DOCUMENT:
                messageDetails.getMessageProperties()
                              .remove(messageProperties.getContentPdfFileName(), fileName);
                break;
            case DETACHED_SIGNATURE:
                messageDetails.getMessageProperties()
                              .remove(messageProperties.getDetachedSignatureFileName(), fileName);
                break;
            default:
        }

        var messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
        storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
    }

    protected void setMessageSentInMessageProperties(File messageFolder) {
        var messageDetails =
            new DefaultMessageProperties(messageFolder, this.messageProperties.getFileName());

        messageDetails.getMessageProperties().put(
            messageProperties.getMessageSentDatetime(),
            DomibusConnectorClientFileSystemUtil.convertDateToProperty(
                new Date())
        );

        var messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
        storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
    }
}
