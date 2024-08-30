/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.io.File;

/**
 * The DomibusConnectorClientFileSystemWriter interface represents a contract for writing
 * messages and files to the file system in the Domibus Connector Client.
 * It defines methods for writing confirmation messages, message files, and deleting files
 * from the file system, as well as updating message status and deleting messages.
 */
public interface DomibusConnectorClientFileSystemWriter {
    void writeConfirmationToFileSystem(
        DomibusConnectorMessageType confirmationMessage, File messageFolder)
        throws DomibusConnectorClientFileSystemException;

    void writeMessageFileToFileSystem(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType, byte[] fileContent)
        throws DomibusConnectorClientFileSystemException;

    void deleteMessageFileFromFileSystem(
        File messageFolder, String fileName,
        DomibusConnectorClientStorageFileType fileType)
        throws DomibusConnectorClientFileSystemException;

    String updateMessageAtStorageToSent(File messageFolder)
        throws DomibusConnectorClientFileSystemException;

    String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir)
        throws DomibusConnectorClientFileSystemException;

    void deleteFromStorage(File messageFolder) throws DomibusConnectorClientFileSystemException;
}
