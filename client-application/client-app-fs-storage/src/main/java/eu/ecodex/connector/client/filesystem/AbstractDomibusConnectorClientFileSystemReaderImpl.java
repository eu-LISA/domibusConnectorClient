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

import eu.ecodex.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This abstract class provides a base implementation of the DomibusConnectorClientFileSystemReader
 * interface for reading messages from a file system.
 */
@SuppressWarnings("squid:S1135")
public abstract class AbstractDomibusConnectorClientFileSystemReaderImpl {
    @Autowired
    private DomibusConnectorClientFSConfigurationProperties properties;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AbstractDomibusConnectorClientFileSystemReaderImpl.class);

    /**
     * Reads all message files from the specified directory that have a filename ending with the
     * provided postfix.
     *
     * @param messagesDir the directory from which to read the messages
     * @param endsWith    the postfix that the filenames should end with
     * @return a list of message files found in the directory that satisfy the postfix condition
     */
    public List<File> readMessagesFromDirWithPostfix(File messagesDir, String endsWith) {
        List<File> messages = new ArrayList<>();

        if (messagesDir.listFiles().length > 0) {
            for (File sub : messagesDir.listFiles()) {
                if (sub.isDirectory()
                    && sub.getName().endsWith(endsWith)) {
                    messages.add(sub);
                }
            }
        }
        return messages;
    }

    /**
     * Reads all valid message files from the specified directory.
     *
     * @param messagesDir the directory from which to read the messages
     * @return a list of valid message files found in the directory
     */
    public List<File> readAllMessagesFromDir(File messagesDir) {
        List<File> messages = new ArrayList<>();

        if (messagesDir.listFiles().length > 0) {
            for (File sub : messagesDir.listFiles()) {
                if (sub.isDirectory() && sub.listFiles() != null && sub.listFiles().length > 0) {
                    messages.add(sub);
                }
            }
        }
        return messages;
    }

    /**
     * Reads a message from the specified folder.
     *
     * @param messageFolder the folder from which to read the message
     * @return the read message as a {@link DomibusConnectorMessageType} object, or null if the
     *      folder is empty or does not exist
     * @throws DomibusConnectorClientFileSystemException if an error occurs while reading or
     *                                                   processing the message folder
     */
    public DomibusConnectorMessageType readMessageFromFolder(File messageFolder)
        throws DomibusConnectorClientFileSystemException {

        if (messageFolder.exists() && messageFolder.isDirectory()
            && messageFolder.listFiles().length > 0) {
            String messageFolderPath = messageFolder.getAbsolutePath();
            LOGGER.info("Start reading message from folder {}", messageFolderPath);

            var message = new DomibusConnectorMessageType();

            try {
                message = processMessageFolderFiles(messageFolder);
            } catch (Exception e) {
                LOGGER.error("#readMessageFromFolder: an error occured!", e);
                throw new DomibusConnectorClientFileSystemException(
                    "Could not process message folder " + messageFolder.getAbsolutePath());
            }

            return message;
        }
        return null;
    }

    /**
     * Loads the content of a file from the specified message folder.
     *
     * @param messageFolder the folder containing the message files
     * @param fileName      the name of the file to load
     * @return the content of the file as a byte array, or an empty byte array if the file is not
     *      found or the folder is empty or does not exist
     */
    public byte[] loadFileContentFromMessageFolder(File messageFolder, String fileName) {
        if (messageFolder.exists() && messageFolder.isDirectory()
            && messageFolder.listFiles().length > 0) {

            String messageFolderPath = messageFolder.getAbsolutePath();
            LOGGER.debug("Start reading file {} from folder {}", fileName, messageFolderPath);

            for (File sub : messageFolder.listFiles()) {
                if (sub.getName().equals(fileName)) {
                    LOGGER.debug(
                        "Found file with name {} and length {}", sub.getName(), sub.length());
                    byte[] content = null;
                    try {
                        content = fileToByteArray(sub);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    return content;
                }
            }
        }
        return new byte[0];
    }

    /**
     * Checks the storage status of a message based on the provided storage location.
     *
     * @param storageLocation the location of the message storage
     * @return the storage status of the message, one of STORED, DELETED, or UNKNOWN
     */
    public DomibusConnectorClientStorageStatus checkStorageStatusOfMessage(String storageLocation) {
        var storageFile = new File(storageLocation);

        // check if the storageLocation is a directory
        if (storageFile.exists() && storageFile.isDirectory()) {
            return DomibusConnectorClientStorageStatus.STORED;
        }

        // None of the above, so the storageLocation does not exist anymore
        return DomibusConnectorClientStorageStatus.DELETED;
    }

    public abstract Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(
        File messageFolder);

    protected abstract DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder)
        throws DomibusConnectorClientFileSystemException;

    protected boolean isFile(String filename, String messageProperty) {
        return messageProperty != null && messageProperty.equals(filename);
    }

    protected boolean isConfirmation(String filename) {
        if (filename != null) {
            if (filename.indexOf(".xml") > 0) {
                var name = filename.substring(0, filename.indexOf(".xml"));
                List<String> confirmationNames =
                    Arrays.stream(DomibusConnectorConfirmationType.values())
                          .map(Enum::name)
                          .toList();
                if (confirmationNames.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected byte[] fileToByteArray(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
}
