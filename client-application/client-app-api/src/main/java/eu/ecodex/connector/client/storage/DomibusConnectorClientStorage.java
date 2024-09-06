/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.storage;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.storage.exception.DomibusConnectorClientStorageException;
import java.util.Map;

/**
 * Interface defining the operations the client requires for handling a storage. Must be implemented
 * if a custom storage implementation should be used by the client. By default the client offers a
 * filesystem based storage solution with the domibusConnectorClientFSStorage module.
 *
 * @author riederb
 */
public interface DomibusConnectorClientStorage {
    /**
     * Checks the storage for new messages that should be submitted to the connector.
     *
     * @return a Map with a key/value pair where the key is the storage location path of the
     *      message, and the value is the message.
     */
    Map<String, DomibusConnectorMessageType> checkStorageForNewMessages();

    /**
     * Loads all messages from the storage.
     *
     * @return a Map with a key/value pair where the key is the storage location path of the
     *      message, and the value is the message.
     */
    Map<String, DomibusConnectorMessageType> getAllStoredMessages();

    /**
     * Stores a message into the storage.
     *
     * @param message - The message to be stored
     * @return The location path where the message has been stored.
     * @throws DomibusConnectorClientStorageException if there is an error related to storing the
     *                                                message
     */
    String storeMessage(DomibusConnectorMessageType message)
        throws DomibusConnectorClientStorageException;

    /**
     * Stores a confirmation received from the connector. Stores the confirmation as XML file at the
     * storage location of the original message.
     *
     * @param message         - The confirmation message received from the connector.
     * @param storageLocation - The storage location path where the original message is stored.
     * @throws DomibusConnectorClientStorageException If there is an error related to storing the
     *                                                confirmation.
     * @throws IllegalArgumentException               If the storage location is invalid or
     *                                                unsupported.
     */
    void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * * Checks, if the storage location path still exists.
     *
     * @param storageLocation The storage location to check.
     * @return The storage status of the given storage location. Possible values are STORED,
     *      DELETED, or UNKNOWN.
     */
    DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation);

    /**
     * Load the content of a file at a storage location.
     *
     * @param storageLocation - The storage location path where to search for the file.
     * @param name            - The name of the file as stored inside the storage location.
     * @return The bytes of the file.
     * @throws DomibusConnectorClientStorageException if there is an error related to retrieving the
     *                                                file content.
     * @throws IllegalArgumentException               if the storage location or name is invalid.
     */
    byte[] loadFileContentFromStorageLocation(String storageLocation, String name)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Browses a storage location path and lists all files contained at the storage location. Only
     * used by the connectorClient's rest API!
     *
     * @param storageLocation - The storage location path where to search for files.
     * @return A Map where the key is the fileName found and the value the type of file.
     * @throws DomibusConnectorClientStorageException If there is an error related to retrieving the
     *                                                content.
     * @throws IllegalArgumentException               If the storage location is invalid or
     *                                                unsupported.
     */
    Map<String, DomibusConnectorClientStorageFileType> listContentAtStorageLocation(
        String storageLocation)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Loads the complete message from the given storage location.
     *
     * @param storageLocation - The storage location path.
     * @return The message loaded from the storage containing all contents found there.
     * @throws DomibusConnectorClientStorageException if there is an error related to retrieving the
     *                                                message
     * @throws IllegalArgumentException               if the storage location is invalid or
     *                                                unsupported
     */
    DomibusConnectorMessageType getStoredMessage(String storageLocation)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Updates a message stored and successfully submitted. Changes parts of the contents stored and
     * renames storage location so the message does not get submitted again.
     *
     * @param storageLocation - The storage location path where the submitted message is stored.
     * @return The new storage location path after renaming.
     * @throws DomibusConnectorClientStorageException if there is an error related to updating the
     *                                                stored message.
     * @throws IllegalArgumentException               if the storage location is invalid or
     *                                                unsupported.
     */
    String updateStoredMessageToSent(String storageLocation)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Adds a file to the message's storage location.
     *
     * @param storageLocation - The storage location path where the submitted message is stored.
     * @param fileName        - The name of the file to be stored including filetype suffix.
     * @param fileType        - The type of file to be stored.
     * @param content         - The content of the file to be stored as bytes.
     * @throws DomibusConnectorClientStorageException If there is an error related to storing the
     *                                                file.
     * @throws IllegalArgumentException               If the storage location is invalid or
     *                                                unsupported.
     */
    void storeFileIntoStorage(
        String storageLocation, String fileName, DomibusConnectorClientStorageFileType fileType,
        byte[] content)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Complete removes everything contained at the given storage location.
     *
     * @param storageLocation - The storage location path that should be removed.
     * @throws DomibusConnectorClientStorageException if there is an error related to deleting the
     *                                                message from the storage.
     * @throws IllegalArgumentException               if the storage location is invalid or
     *                                                unsupported.
     */
    void deleteMessageFromStorage(String storageLocation)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;

    /**
     * Deletes a certain file from the storage location.
     *
     * @param storageLocation - The storage location path the given file should be removed from.
     * @param fileName        - The name of the file that should be removed.
     * @param fileType        - The type of the file. May result in changes of contents of the
     *                        message details.
     * @throws DomibusConnectorClientStorageException if there is an error related to deleting the
     *                                                file from the storage.
     * @throws IllegalArgumentException               if the storage location is invalid or
     *                                                unsupported.
     */
    void deleteFileFromStorage(
        String storageLocation, String fileName, DomibusConnectorClientStorageFileType fileType)
        throws DomibusConnectorClientStorageException, IllegalArgumentException;
}
