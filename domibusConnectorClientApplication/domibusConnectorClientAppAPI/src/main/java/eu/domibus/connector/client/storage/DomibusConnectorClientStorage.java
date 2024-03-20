package eu.domibus.connector.client.storage;

import java.util.Map;

import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface defining the operations the client requires for handling a storage. Must be implemented if a custom storage implementation should be used by the client.
 * By default the client offers a filesystem based storage solution with the domibusConnectorClientFSStorage module.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientStorage {

	/**
	 * Checks the storage for new messages that should be submitted to the connector.
	 * 
	 * @return a Map with a key/value pair where the key is the storage location path of the message, and the value is the message.
	 */
	Map<String, DomibusConnectorMessageType> checkStorageForNewMessages();

	/**
	 * Loads all messages from the storage.
	 * 
	 * @return a Map with a key/value pair where the key is the storage location path of the message, and the value is the message.
	 */
	Map<String, DomibusConnectorMessageType> getAllStoredMessages();
	
	/**
	 * Stores a message into the storage.
	 * 
	 * @param message - The message to be stored
	 * @return The location path where the message has been stored.
	 * @throws DomibusConnectorClientStorageException
	 */
	String storeMessage(DomibusConnectorMessageType message) 
			throws DomibusConnectorClientStorageException;
	
	/**
	 * Stores a confirmation received from the connector.
	 * Stores the confirmation as XML file at the storage location of the original message.
	 * 
	 * @param message - The confirmation message received from the connector.
	 * @param storageLocation - The storage location path where the original message is stored.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	
	/**
	 * Checks, if the storage location path still exists.
	 * 
	 * @param storageLocation
	 * @return
	 */
	DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation);
	
	
	/**
	 * Load the content of a file at a storage location.
	 * 
	 * @param storageLocation - The storage location path where to search for the file.
	 * @param name - The name of the file as stored inside the storage location.
	 * @return The bytes of the file.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	byte[] loadFileContentFromStorageLocation(String storageLocation, String name) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	/**
	 * Browses a storage location path and lists all files contained at the storage location. Only used by the connectorClient's rest API!
	 * 
	 * @param storageLocation - The storage location path where to search for files.
	 * @return A Map where the key is the fileName found and the value the type of file. 
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	Map<String, DomibusConnectorClientStorageFileType> listContentAtStorageLocation(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	/**
	 * Loads the complete message from the given storage location.
	 * 
	 * @param storageLocation - The storage location path.
	 * @return The message loaded from the storage containing all contents found there.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	DomibusConnectorMessageType getStoredMessage(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	/**
	 * Updates a message stored and successfully submitted. Changes parts of the contents stored and renames storage location so the message
	 * does not get submitted again.
	 * 
	 * @param storageLocation - The storage location path where the submitted message is stored.
	 * @return The new storage location path after renaming.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	String updateStoredMessageToSent(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	/**
	 * Adds a file to the message's storage location.
	 * 
	 * @param storageLocation - The storage location path where the submitted message is stored.
	 * @param fileName - The name of the file to be stored including filetype suffix.
	 * @param fileType - The type of file to be stored.
	 * @param content - The content of the file to be stored as bytes.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	void storeFileIntoStorage(String storageLocation, String fileName, DomibusConnectorClientStorageFileType fileType, byte[] content) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	/**
	 * Complete removes everything contained at the given storage location.
	 * 
	 * @param storageLocation - The storage location path that should be removed.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	void deleteMessageFromStorage(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	/**
	 * Deletes a certain file from the storage location.
	 * 
	 * @param storageLocation - The storage location path the given file should be removed from.
	 * @param fileName - The name of the file that should be removed.
	 * @param fileType - The type of the file. May result in changes of contents of the message details.
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	void deleteFileFromStorage(String storageLocation, String fileName,	DomibusConnectorClientStorageFileType fileType) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
}
