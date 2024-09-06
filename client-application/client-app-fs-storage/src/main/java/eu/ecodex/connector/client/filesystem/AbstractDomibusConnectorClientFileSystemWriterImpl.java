/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem;

import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Abstract class that provides common functionality for writing files to the file system for the
 * Domibus Connector Client.
 */
public abstract class AbstractDomibusConnectorClientFileSystemWriterImpl {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AbstractDomibusConnectorClientFileSystemWriterImpl.class);
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;

    /**
     * Writes a message confirmation file to the file system.
     *
     * @param confirmationMessage The message containing the confirmation to be written.
     * @param messageFolder       The folder where the confirmation file will be stored.
     * @throws DomibusConnectorClientFileSystemException if an error occurs while writing the
     *                                                   confirmation file to the file system.
     */
    public void writeConfirmationToFileSystem(
        DomibusConnectorMessageType confirmationMessage, File messageFolder)
        throws DomibusConnectorClientFileSystemException {
        var confirmation = confirmationMessage.getMessageConfirmations().getFirst();
        var type = confirmation.getConfirmationType().name();

        var path = messageFolder.getAbsolutePath() + File.separator + type
            + properties.getXmlFileExtension();

        LOGGER.debug("Create confirmation file {}", path);
        var evidenceXml = new File(path);
        try {
            var xmlBytes =
                ConversionTools.convertXmlSourceToByteArray(confirmation.getConfirmation());
            byteArrayToFile(xmlBytes, evidenceXml);
        } catch (IOException e) {
            throw new DomibusConnectorClientFileSystemException(
                "Could not create file "
                    + evidenceXml.getAbsolutePath(), e);
        }
    }

    /**
     * Writes a message file to the file system.
     *
     * @param messageFolder The folder where the message file will be stored
     * @param fileName      The name of the message file
     * @param fileType      The type of the message file
     * @param fileContent   The content of the message file
     * @throws DomibusConnectorClientFileSystemException if an error occurs while writing the
     *                                                   message file to the file system
     */
    public void writeMessageFileToFileSystem(
        File messageFolder, String fileName, DomibusConnectorClientStorageFileType fileType,
        byte[] fileContent) throws DomibusConnectorClientFileSystemException {
        putFileToMessageProperties(messageFolder, fileName, fileType);

        try {
            createFile(messageFolder, fileName, fileContent);
        } catch (IOException e1) {
            throw new DomibusConnectorClientFileSystemException(
                "Could not create file " + fileName + " at message folder "
                    + messageFolder.getAbsolutePath(), e1);
        }
    }

    protected abstract void putFileToMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType);

    /**
     * Deletes a message file from the file system.
     *
     * @param messageFolder The folder where the message file is stored.
     * @param fileName      The name of the message file to be deleted.
     * @param fileType      The type of the message file.
     * @throws DomibusConnectorClientFileSystemException if an error occurs while deleting the
     *                                                   message file from the file system.
     */
    public void deleteMessageFileFromFileSystem(
        File messageFolder, String fileName, DomibusConnectorClientStorageFileType fileType)
        throws DomibusConnectorClientFileSystemException {

        removeFileFromMessageProperties(messageFolder, fileName, fileType);

        deleteFile(messageFolder, fileName);
    }

    protected abstract void removeFileFromMessageProperties(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType);

    /**
     * Updates the message at storage to the "sent" state.
     *
     * @param messageFolder The folder where the message is stored.
     * @return The absolute path of the message folder or the new folder path if the folder was
     *      renamed.
     */
    public String updateMessageAtStorageToSent(File messageFolder) {
        setMessageSentInMessageProperties(messageFolder);

        if (messageFolder.getAbsolutePath().endsWith(properties.getMessageReadyPostfix())) {
            var newFolderName = messageFolder.getAbsolutePath().substring(
                0,
                messageFolder.getAbsolutePath().length() - properties.getMessageReadyPostfix()
                                                                     .length()
            );
            var newMessageFolder = new File(newFolderName);
            messageFolder.renameTo(newMessageFolder);
            return newFolderName;
        }
        return messageFolder.getAbsolutePath();
    }

    protected abstract void setMessageSentInMessageProperties(File messageFolder);

    /**
     * Deletes a message folder from the file system, including its contents.
     *
     * @param messageFolder The folder to be deleted.
     * @throws DomibusConnectorClientFileSystemException if an error occurs while deleting the
     *                                                   message folder and/or its contents.
     */
    public void deleteFromStorage(File messageFolder)
        throws DomibusConnectorClientFileSystemException {
        try {
            deleteDirectory(messageFolder);
        } catch (Exception e) {
            throw new DomibusConnectorClientFileSystemException(
                "Message folder and/or its contents could not be deleted! ", e);
        }
    }

    public abstract String writeMessageToFileSystem(
        DomibusConnectorMessageType message, File messagesDir)
        throws DomibusConnectorClientFileSystemException;

    protected void writeBusinessContentToFS(
        File messageFolder, DomibusConnectorMessageContentType messageContent,
        String fileName) {
        try {
            byte[] content =
                ConversionTools.convertXmlSourceToByteArray(messageContent.getXmlContent());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    "Business content XML before written to file: {}", new String(content));
            }
            createFile(messageFolder, fileName, content);
        } catch (IOException | TransformerFactoryConfigurationError e) {
            LOGGER.error(
                "Could not process business content file {} at messageFolder {}", fileName,
                messageFolder.getAbsolutePath(), e
            );
        }
    }

    protected void writeBusinessDocumentToFS(
        File messageFolder, String fileName,
        DomibusConnectorMessageContentType messageContent) {

        byte[] document;
        try {
            document = ConversionTools.convertDataHandlerToByteArray(
                messageContent.getDocument().getDocument());
            createFile(messageFolder, fileName, document);
        } catch (IOException e) {
            LOGGER.error(
                "Could not process business document file {} at messageFolder {}", fileName,
                messageFolder.getAbsolutePath(), e
            );
        }
    }

    protected String writeDetachedSignatureToFS(
        File messageFolder,
        DomibusConnectorDetachedSignatureType detachedSignature) {
        String fileName2;
        if (StringUtils.hasText(detachedSignature.getDetachedSignatureName())
            && !detachedSignature.getDetachedSignatureName().equals(
            properties.getDefaultDetachedSignatureFileName())) {
            fileName2 = detachedSignature.getDetachedSignatureName();
        } else {
            fileName2 = properties.getDefaultDetachedSignatureFileName();
            if (detachedSignature.getMimeType()
                                 .equals(DomibusConnectorDetachedSignatureMimeType.XML)) {
                fileName2 += properties.getXmlFileExtension();
            } else if (detachedSignature.getMimeType()
                                        .equals(DomibusConnectorDetachedSignatureMimeType.PKCS_7)) {
                fileName2 += properties.getPkcs7FileExtension();
            }
        }
        try {
            createFile(messageFolder, fileName2, detachedSignature.getDetachedSignature());
        } catch (IOException e) {
            LOGGER.error(
                "Could not process detached signature file {} at messageFolder {}", fileName2,
                messageFolder.getAbsolutePath(), e
            );
        }
        return fileName2;
    }

    protected void writeAttachmentsToFS(DomibusConnectorMessageType message, File messageFolder) {
        for (DomibusConnectorMessageAttachmentType attachment : message.getMessageAttachments()) {
            try {
                var attachmentBytes =
                    ConversionTools.convertDataHandlerToByteArray(attachment.getAttachment());
                createFile(messageFolder, attachment.getName(), attachmentBytes);
            } catch (IOException e) {
                LOGGER.error(
                    "Could not process business attachment file {} at messageFolder {}",
                    attachment.getName(), messageFolder.getAbsolutePath(), e
                );
            }
        }
    }

    protected void writeConfirmationsToFS(DomibusConnectorMessageType message, File messageFolder) {
        for (var confirmation : message.getMessageConfirmations()) {
            var fileName =
                confirmation.getConfirmationType().name() + properties.getXmlFileExtension();
            try {
                var confirmationBytes =
                    ConversionTools.convertXmlSourceToByteArray(confirmation.getConfirmation());
                createFile(messageFolder, fileName, confirmationBytes);
            } catch (IOException e) {
                LOGGER.error(
                    "Could not process confirmation file {} at messageFolder {}", fileName,
                    messageFolder.getAbsolutePath(), e
                );
            }
        }
    }

    private void deleteDirectory(File directory) throws Exception {
        List<File> filesToBeDeleted = new ArrayList<>();
        Collections.addAll(filesToBeDeleted, Objects.requireNonNull(directory.listFiles()));

        if (!filesToBeDeleted.isEmpty()) {
            for (File sub : filesToBeDeleted) {
                if (sub.isDirectory()) {
                    deleteDirectory(sub);
                } else {
                    sub.delete();
                }
            }
        }

        directory.delete();
    }

    private void createFile(File messageFolder, String fileName, byte[] content)
        throws IOException {
        var filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
        var file = new File(filePath);
        if (!file.exists()) {
            LOGGER.debug("Create file {}", filePath);
            byteArrayToFile(content, file);
        }
    }

    private void deleteFile(File messageFolder, String fileName)
        throws DomibusConnectorClientFileSystemException {
        String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
        var file = new File(filePath);
        if (file.exists()) {
            LOGGER.debug("delete file {}", filePath);
            if (!file.delete()) {
                throw new DomibusConnectorClientFileSystemException(
                    "File " + file.getAbsolutePath() + " could not be deleted!");
            }
        }
    }

    private void byteArrayToFile(byte[] data, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        var fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    protected File createMessageFolder(
        DomibusConnectorMessageType message, File messagesDir, boolean createIfNonExistent)
        throws DomibusConnectorClientFileSystemException {

        String messageId =
            message.getMessageDetails().getEbmsMessageId() != null
                && !message.getMessageDetails().getEbmsMessageId().isEmpty()
                ? message.getMessageDetails().getEbmsMessageId()
                : message.getMessageDetails().getBackendMessageId();
        var pathname = new StringBuilder()
            .append(messagesDir.getAbsolutePath())
            .append(File.separator)
            .append(DomibusConnectorClientFileSystemUtil.getMessageFolderName(message, messageId))
            .toString();
        var messageFolder = new File(pathname);
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
    }
}
