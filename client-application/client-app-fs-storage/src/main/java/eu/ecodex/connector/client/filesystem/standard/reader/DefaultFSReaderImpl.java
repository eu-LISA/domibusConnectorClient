/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.standard.reader;

import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import eu.ecodex.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemReaderImpl;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.ecodex.connector.client.filesystem.standard.DefaultMessageProperties;
import eu.ecodex.connector.client.filesystem.standard.DomibusConnectorClientFSMessageProperties;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * The DefaultFSReaderImpl class is an implementation of the DomibusConnectorClientFileSystemReader
 * interface.
 */
@SuppressWarnings("squid:S1135")
public class DefaultFSReaderImpl extends AbstractDomibusConnectorClientFileSystemReaderImpl
    implements DomibusConnectorClientFileSystemReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFSReaderImpl.class);
    public static final String FOUND_CONTENT_XML_FILE_WITH_NAME =
        "Found content xml file with name {}";
    public static final String FOUND_CONTENT_PDF_FILE_WITH_NAME =
        "Found content pdf file with name {}";
    public static final String FOUND_DETACHED_SIGNATURE_FILE_WITH_NAME =
        "Found detached signature file with name {}";
    @Autowired
    private DomibusConnectorClientFSMessageProperties messageProperties;
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;

    @Override
    public List<File> readUnsentMessages(File outgoingMessagesDir) {
        String messageReadyPostfix = properties.getMessageReadyPostfix();
        if (messageReadyPostfix == null) {
            messageReadyPostfix = "";
        }
        LOGGER.debug(
            "#readUnsentMessages: Searching for folders with ending {}", messageReadyPostfix);
        List<File> messagesUnsent = new ArrayList<>();

        if (outgoingMessagesDir.listFiles().length > 0) {
            for (var sub : outgoingMessagesDir.listFiles()) {
                if (sub.isDirectory()
                    && sub.getName().endsWith(messageReadyPostfix)) {
                    messagesUnsent.add(sub);
                }
            }
        }

        return messagesUnsent;
    }

    /**
     * Retrieves the list of files in the given message folder along with their corresponding file
     * types.
     *
     * @param messageFolder The folder containing the message files.
     * @return A map of file names and their corresponding file types.
     */
    public Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(
        File messageFolder) {
        Map<String, DomibusConnectorClientStorageFileType> files =
            new HashMap<>();
        if (messageFolder.exists() && messageFolder.isDirectory()
            && messageFolder.listFiles().length > 0) {

            var messageDetails =
                new DefaultMessageProperties(messageFolder, this.messageProperties.getFileName());

            for (var sub : messageFolder.listFiles()) {
                if (!sub.getName().equals(messageProperties.getFileName())) {
                    if (isFile(sub.getName(), messageDetails.getMessageProperties().getProperty(
                        messageProperties.getContentXmlFileName()))) {
                        LOGGER.debug(FOUND_CONTENT_XML_FILE_WITH_NAME, sub.getName());
                        files.put(
                            sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_CONTENT
                        );
                    } else if (isFile(sub.getName(), messageDetails
                        .getMessageProperties().getProperty(
                            messageProperties.getContentPdfFileName()))
                    ) {
                        LOGGER.debug(FOUND_CONTENT_PDF_FILE_WITH_NAME, sub.getName());
                        files.put(
                            sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_DOCUMENT
                        );
                    } else if (isFile(sub.getName(), messageDetails
                        .getMessageProperties()
                        .getProperty(
                            messageProperties.getDetachedSignatureFileName()))
                    ) {
                        LOGGER.debug(FOUND_DETACHED_SIGNATURE_FILE_WITH_NAME, sub.getName());
                        files.put(
                            sub.getName(),
                            DomibusConnectorClientStorageFileType.DETACHED_SIGNATURE
                        );
                    } else if (isConfirmation(sub.getName())) {
                        LOGGER.debug("Found confirmation file {}", sub.getName());
                        files.put(
                            sub.getName(), DomibusConnectorClientStorageFileType.CONFIRMATION);
                    } else {
                        LOGGER.debug("Found attachment file {}", sub.getName());
                        files.put(
                            sub.getName(),
                            DomibusConnectorClientStorageFileType.BUSINESS_ATTACHMENT
                        );
                    }
                }
            }
        }
        return files;
    }

    protected DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder)
        throws DomibusConnectorClientFileSystemException {
        var message = new DomibusConnectorMessageType();

        var messageDetails =
            new DefaultMessageProperties(workMessageFolder, this.messageProperties.getFileName());

        var msgDetails = convertMessagePropertiesToMessageDetails(messageDetails);
        message.setMessageDetails(msgDetails);

        var attachmentCount = 1;

        var messageContent = new DomibusConnectorMessageContentType();
        var document = new DomibusConnectorMessageDocumentType();

        for (var sub : workMessageFolder.listFiles()) {
            if (!sub.getName().equals(messageDetails.getMessageProperties()
                                                   .getProperty(messageProperties.getFileName()))) {
                if (isFile(sub.getName(), messageDetails.getMessageProperties().getProperty(
                    messageProperties.getContentXmlFileName()))) {
                    LOGGER.debug(FOUND_CONTENT_XML_FILE_WITH_NAME, sub.getName());
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            byte[] xmlBytes = fileToByteArray(sub);
                            LOGGER.debug(
                                "Business content XML after read from file: {}",
                                new String(xmlBytes)
                            );
                        }
                        messageContent.setXmlContent(
                            ConversionTools.convertFileToStreamSource(sub));
                    } catch (IOException e) {
                        throw new DomibusConnectorClientFileSystemException(
                            "Exception creating Source object out of file " + sub.getName());
                    }
                } else if (isFile(sub.getName(), messageDetails.getMessageProperties().getProperty(
                    messageProperties.getContentPdfFileName()))) {
                    LOGGER.debug(FOUND_CONTENT_PDF_FILE_WITH_NAME, sub.getName());
                    document.setDocument(
                        ConversionTools.convertFileToDataHandler(sub, "application/octet-stream"));
                    document.setDocumentName(sub.getName());
                } else if (isFile(sub.getName(), messageDetails.getMessageProperties().getProperty(
                    messageProperties.getDetachedSignatureFileName()))) {
                    LOGGER.debug(FOUND_DETACHED_SIGNATURE_FILE_WITH_NAME, sub.getName());
                    try {
                        var det = new DomibusConnectorDetachedSignatureType();
                        det.setDetachedSignature(fileToByteArray(sub));
                        det.setDetachedSignatureName(sub.getName());
                        if (sub.getName().endsWith(properties.getXmlFileExtension())) {
                            det.setMimeType(DomibusConnectorDetachedSignatureMimeType.XML);
                        } else if (sub.getName()
                                      .endsWith(properties.getPkcs7FileExtension())) {
                            det.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);
                        } else {
                            det.setMimeType(DomibusConnectorDetachedSignatureMimeType.BINARY);
                        }
                        document.setDetachedSignature(det);
                    } catch (IOException e) {
                        throw new DomibusConnectorClientFileSystemException(
                            "Exception loading detached signature into byte array from file "
                                + sub.getName());
                    }
                } else if (isConfirmation(sub.getName())) {
                    var confirmation = new DomibusConnectorMessageConfirmationType();
                    confirmation.setConfirmation(ConversionTools.convertFileToStreamSource(sub));
                    if (sub.getName().indexOf(".xml") > 0) {
                        var name = sub.getName().substring(0, sub.getName().indexOf(".xml"));
                        var valueOf = DomibusConnectorConfirmationType.valueOf(name);
                        if (valueOf != null) {
                            confirmation.setConfirmationType(valueOf);
                        }
                    }
                    message.getMessageConfirmations().add(confirmation);
                } else if (!isFile(sub.getName(), this.messageProperties.getFileName())) {
                    LOGGER.debug("Processing attachment File {}", sub.getName());

                    byte[] attachmentData;
                    try {
                        attachmentData = fileToByteArray(sub);
                    } catch (IOException e) {
                        throw new DomibusConnectorClientFileSystemException(
                            "Exception loading attachment into byte array from file "
                                + sub.getName());
                    }
                    var attachmentId = properties.getAttachmentIdPrefix() + attachmentCount;

                    if (!ArrayUtils.isEmpty(attachmentData)) {
                        var attachment = new DomibusConnectorMessageAttachmentType();
                        attachment.setAttachment(ConversionTools.convertFileToDataHandler(
                            sub,
                            "application/octet-stream"
                        ));
                        attachment.setIdentifier(attachmentId);
                        attachment.setName(sub.getName());
                        attachmentCount++;
                        attachment.setMimeType(
                            new MimetypesFileTypeMap().getContentType(sub.getName()));

                        LOGGER.debug("Add attachment {}", attachment);
                        message.getMessageAttachments().add(attachment);
                    }
                }
            }
        }

        if (document.getDocument() != null) {
            messageContent.setDocument(document);
        }

        if (messageContent.getXmlContent() != null) {
            message.setMessageContent(messageContent);
        }

        return message;
    }

    private DomibusConnectorMessageDetailsType convertMessagePropertiesToMessageDetails(
        DefaultMessageProperties properties) {
        var messageDetails = new DomibusConnectorMessageDetailsType();

        messageDetails.setEbmsMessageId(
            properties.getMessageProperties().getProperty(messageProperties.getEbmsMessageId()));

        messageDetails.setFinalRecipient(
            properties.getMessageProperties().getProperty(messageProperties.getFinalRecipient()));
        messageDetails.setOriginalSender(
            properties.getMessageProperties().getProperty(messageProperties.getOriginalSender()));
        if (StringUtils.hasText(
            properties.getMessageProperties()
                      .getProperty(messageProperties.getBackendMessageId()))) {
            messageDetails.setBackendMessageId(properties.getMessageProperties().getProperty(
                messageProperties.getBackendMessageId()));
        }

        var fromPartyId =
            properties.getMessageProperties().getProperty(messageProperties.getFromPartyId());
        var fromPartyRole =
            properties.getMessageProperties().getProperty(messageProperties.getFromPartyRole());

        var fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(fromPartyId);
        fromParty.setRole(fromPartyRole);
        messageDetails.setFromParty(fromParty);

        var toPartyId =
            properties.getMessageProperties().getProperty(messageProperties.getToPartyId());
        String toPartyRole =
            properties.getMessageProperties().getProperty(messageProperties.getToPartyRole());
        var toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(toPartyId);
        toParty.setRole(toPartyRole);
        messageDetails.setToParty(toParty);

        var action = properties.getMessageProperties().getProperty(messageProperties.getAction());
        var domibusConnectorAction = new DomibusConnectorActionType();
        domibusConnectorAction.setAction(action);
        messageDetails.setAction(domibusConnectorAction);

        var service = properties.getMessageProperties().getProperty(messageProperties.getService());
        var domibusConnectorService = new DomibusConnectorServiceType();
        domibusConnectorService.setService(service);
        domibusConnectorService.setServiceType(
            properties.getMessageProperties().getProperty(messageProperties.getServiceType())
        );
        messageDetails.setService(domibusConnectorService);

        var conversationId =
            properties.getMessageProperties().getProperty(messageProperties.getConversationId());
        if (StringUtils.hasText(conversationId)) {
            messageDetails.setConversationId(conversationId);
        }

        return messageDetails;
    }
}
