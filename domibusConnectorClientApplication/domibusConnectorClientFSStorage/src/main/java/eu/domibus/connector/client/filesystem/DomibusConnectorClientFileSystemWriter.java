package eu.domibus.connector.client.filesystem;

import java.io.File;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientFileSystemWriter {

	void writeConfirmationToFileSystem(DomibusConnectorMessageType confirmationMessage, File messageFolder)
			throws DomibusConnectorClientFileSystemException;

	void writeMessageFileToFileSystem(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType, byte[] fileContent)
			throws DomibusConnectorClientFileSystemException;

	void deleteMessageFileFromFileSystem(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType) throws DomibusConnectorClientFileSystemException;

	String updateMessageAtStorageToSent(File messageFolder) throws DomibusConnectorClientFileSystemException;

	String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir)
			throws DomibusConnectorClientFileSystemException;

	void deleteFromStorage(File messageFolder) throws DomibusConnectorClientFileSystemException;

}