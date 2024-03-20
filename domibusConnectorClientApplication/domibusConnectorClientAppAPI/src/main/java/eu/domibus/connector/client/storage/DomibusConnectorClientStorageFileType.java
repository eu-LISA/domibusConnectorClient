package eu.domibus.connector.client.storage;

/**
 * This enumeration states the different types of files that may be stored for a message in the client's storage.
 * 
 * @author riederb
 *
 */
public enum DomibusConnectorClientStorageFileType {
	BUSINESS_CONTENT, BUSINESS_DOCUMENT, BUSINESS_ATTACHMENT, DETACHED_SIGNATURE, CONFIRMATION;
}
