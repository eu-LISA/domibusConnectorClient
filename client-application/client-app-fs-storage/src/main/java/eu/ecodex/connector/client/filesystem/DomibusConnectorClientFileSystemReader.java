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

import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * The {@code DomibusConnectorClientFileSystemReader} interface defines the contract for reading and
 * retrieving messages and file information from the file system in the Domibus Connector Client.
 *
 * @see DomibusConnectorMessageType
 * @see DomibusConnectorClientStorageFileType
 * @see DomibusConnectorClientStorageStatus
 * @see DomibusConnectorClientFileSystemException
 */
public interface DomibusConnectorClientFileSystemReader {
    List<File> readUnsentMessages(File outgoingMessagesDir);

    List<File> readMessagesFromDirWithPostfix(File messagesDir, String endsWith);

    List<File> readAllMessagesFromDir(File messagesDir);

    DomibusConnectorMessageType readMessageFromFolder(File messageFolder)
        throws DomibusConnectorClientFileSystemException;

    Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(
        File messageFolder);

    byte[] loadFileContentFromMessageFolder(File messageFolder, String fileName);

    DomibusConnectorClientStorageStatus checkStorageStatusOfMessage(String storageLocation);
}
