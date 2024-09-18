/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.isupport.reader;

import eu.ecodex.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemReaderImpl;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.ecodex.connector.client.filesystem.isupport.ISupportFSMessageProperties;
import eu.ecodex.connector.client.filesystem.isupport.sbdh.SBDHJaxbConverter;
import eu.ecodex.connector.client.filesystem.isupport.sbdh.model.StandardBusinessDocumentHeader;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.ecodex.connector.domain.transition.DomibusConnectorActionType;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import eu.ecodex.connector.domain.transition.tools.ConversionTools;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * {@code ISupportFSReaderImpl} is a class that implements the
 * {@code DomibusConnectorClientFileSystemReader} interface. It provides methods for reading unsent
 * messages and retrieving file lists from a message folder.
 */
@Component
@Validated
@Valid
@Profile("iSupport")
public class ISupportFSReaderImpl extends AbstractDomibusConnectorClientFileSystemReaderImpl
    implements DomibusConnectorClientFileSystemReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ISupportFSReaderImpl.class);
    public static final String FOUND_CONTENT_XML_FILE_WITH_NAME =
        "Found content xml file with name {}";
    @Autowired
    private ISupportFSMessageProperties messageProperties;
    @Autowired
    private SBDHJaxbConverter sbdhConverter;
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;

    @Override
    public List<File> readUnsentMessages(File messagesDir) {
        var outgoingMessagesDir = new File(messagesDir, messageProperties.getISupportOutgoingDir());

        if (!outgoingMessagesDir.exists()) {
            outgoingMessagesDir.mkdir();
            if (!outgoingMessagesDir.exists()) {
                LOGGER.error("Problem with directories.");
                return Collections.emptyList();
            }
        }

        List<File> messagesUnsent = new ArrayList<>();

        if (outgoingMessagesDir.listFiles().length > 0) {
            for (File sub : outgoingMessagesDir.listFiles()) {
                if (sub.isDirectory()) {
                    var processedFile = new File(sub, messageProperties.getProcessedFileName());
                    if (!processedFile.exists()) {
                        messagesUnsent.add(sub);
                    }
                }
            }
        }

        return messagesUnsent;
    }

    @Override
    public Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(
        File messageFolder) {
        Map<String, DomibusConnectorClientStorageFileType> files = new HashMap<>();
        if (messageFolder.canRead() && messageFolder.isDirectory()
            && messageFolder.listFiles().length > 0) {

            for (File sub : messageFolder.listFiles()) {
                if (sub.getName().equals(messageProperties.getFileName())) {
                    LOGGER.debug(FOUND_CONTENT_XML_FILE_WITH_NAME, sub.getName());
                    files.put(
                        sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_CONTENT);
                } else if (isConfirmation(sub.getName())) {
                    LOGGER.debug("Found confirmation file {}", sub.getName());
                    files.put(sub.getName(), DomibusConnectorClientStorageFileType.CONFIRMATION);
                } else {
                    LOGGER.debug("Found attachment file {}", sub.getName());
                    files.put(
                        sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_ATTACHMENT);
                }
            }
        }
        return files;
    }

    @Override
    protected DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder)
        throws DomibusConnectorClientFileSystemException {
        var message = new DomibusConnectorMessageType();

        StandardBusinessDocumentHeader sbdh;
        try {
            sbdh = sbdhConverter.getSBDH(workMessageFolder, this.messageProperties.getFileName());
        } catch (JAXBException | IOException e1) {
            throw new RuntimeException();
        }

        var msgDetails = convertSBDHToMessageDetails(sbdh, workMessageFolder.getName());
        message.setMessageDetails(msgDetails);

        int attachmentCount = 1;

        var messageContent = new DomibusConnectorMessageContentType();
        var document = new DomibusConnectorMessageDocumentType();

        for (var sub : workMessageFolder.listFiles()) {

            if (isFile(sub.getName(), this.messageProperties.getFileName())) {
                LOGGER.debug(FOUND_CONTENT_XML_FILE_WITH_NAME, sub.getName());
                try {
                    if (LOGGER.isDebugEnabled()) {
                        byte[] xmlBytes = fileToByteArray(sub);
                        LOGGER.debug(
                            "Business content XML after read from file: {}", new String(xmlBytes));
                    }
                    messageContent.setXmlContent(ConversionTools.convertFileToStreamSource(sub));
                } catch (IOException e) {
                    throw new DomibusConnectorClientFileSystemException(
                        "Exception creating Source object out of file " + sub.getName());
                }
                LOGGER.debug("Set {} as document name", workMessageFolder.getName());
                document.setDocumentName(workMessageFolder.getName());
                document.setDocument(ConversionTools.convertFileToDataHandler(sub, "text/xml"));
            } else if (isConfirmation(sub.getName())) {
                var confirmation =
                    new DomibusConnectorMessageConfirmationType();
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
                        "Exception loading attachment into byte array from file " + sub.getName());
                }
                String attachmentId = properties.getAttachmentIdPrefix() + attachmentCount;

                if (!ArrayUtils.isEmpty(attachmentData)) {
                    var attachment = new DomibusConnectorMessageAttachmentType();

                    attachment.setAttachment(
                        ConversionTools.convertFileToDataHandler(sub, "application/octet-stream")
                    );
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

        if (document.getDocument() != null) {
            messageContent.setDocument(document);
        }

        if (messageContent.getXmlContent() != null) {
            message.setMessageContent(messageContent);
        }

        var messageProcessedFile =
            new File(workMessageFolder, messageProperties.getProcessedFileName());
        try {
            messageProcessedFile.createNewFile();
        } catch (IOException e) {
            throw new DomibusConnectorClientFileSystemException(
                "File to mark message as processed could not be created for message "
                    + workMessageFolder.getPath());
        }

        return message;
    }

    private DomibusConnectorMessageDetailsType convertSBDHToMessageDetails(
        StandardBusinessDocumentHeader sbdh, String messageFolderName) {
        var messageDetails = new DomibusConnectorMessageDetailsType();

        messageDetails.setFinalRecipient(
            sbdh.getTransport().getReceiver().getContactInformation().getContact()
        );
        messageDetails.setOriginalSender(
            sbdh.getTransport().getSender().getContactInformation().getContact()
        );

        messageDetails.setBackendMessageId(messageFolderName);

        String fromPartyId = sbdh.getTransport().getSender().getIdentifier();
        String fromPartyIdType = messageProperties.getFromPartyIdType();
        String fromPartyRole = messageProperties.getFromPartyRole();

        var fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(fromPartyId);
        fromParty.setPartyIdType(fromPartyIdType);
        fromParty.setRole(fromPartyRole);
        messageDetails.setFromParty(fromParty);

        String toPartyId = sbdh.getTransport().getReceiver().getIdentifier();
        String toPartyIdType = messageProperties.getToPartyIdType();
        String toPartyRole = messageProperties.getToPartyRole();

        var toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(toPartyId);
        toParty.setPartyIdType(toPartyIdType);
        toParty.setRole(toPartyRole);
        messageDetails.setToParty(toParty);

        String action = messageProperties.getAction();
        var domibusConnectorAction = new DomibusConnectorActionType();
        domibusConnectorAction.setAction(action);
        messageDetails.setAction(domibusConnectorAction);

        String service = messageProperties.getService();
        String serviceType = messageProperties.getServiceType();
        var domibusConnectorService = new DomibusConnectorServiceType();
        domibusConnectorService.setService(service);
        domibusConnectorService.setServiceType(serviceType);

        messageDetails.setService(domibusConnectorService);

        String conversationId = sbdh.getTransport().getCaseId();
        if (StringUtils.hasText(conversationId)) {
            messageDetails.setConversationId(conversationId);
        }

        return messageDetails;
    }
}
