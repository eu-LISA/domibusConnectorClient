package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientFileSystemReader {

	List<File> readUnsentMessages(File outgoingMessagesDir);

	List<File> readMessagesFromDirWithPostfix(File messagesDir, String endsWith);

	List<File> readAllMessagesFromDir(File messagesDir);

	DomibusConnectorMessageType readMessageFromFolder(File messageFolder)
			throws DomibusConnectorClientFileSystemException;

	Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(File messageFolder);

	byte[] loadFileContentFromMessageFolder(File messageFolder, String fileName);

	DomibusConnectorClientStorageStatus checkStorageStatusOfMessage(String storageLocation);

}