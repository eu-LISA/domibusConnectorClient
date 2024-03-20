package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;

public interface DomibusConnectorClientAppBackend extends DomibusConnectorClientBackend {

	/**
	 * This method triggers the submission of a prepared and stored message. The message gets completely loaded out of the storage.
	 * Therefore it is important that the message entirely is stored before triggering this method.
	 * 
	 * @param storageLocation - The path in the storage where the message is placed.
	 * @throws DomibusConnectorClientBackendException
	 * @throws DomibusConnectorClientStorageException
	 * @throws IllegalArgumentException
	 */
	void submitStoredClientBackendMessage(String storageLocation) throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException, IllegalArgumentException;
	
}
