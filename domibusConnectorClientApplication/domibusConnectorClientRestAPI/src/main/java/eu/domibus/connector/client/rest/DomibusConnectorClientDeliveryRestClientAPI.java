package eu.domibus.connector.client.rest;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;

/**
 * This interface should be used if a backend application should receive messages via push. In this case the backend application
 * must implement a REST service that implements this interface. Certain properties have to be set properly that the domibusConnectorClient
 * can initialize a REST client that also implements this interface.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientDeliveryRestClientAPI {
	
	/**
	 * This method is called by the domibusConnectorClient to push a new confirmation message to the backend application.
	 * 
	 * @param newConfirmationMessage a client message object holding the new confirmation received by the domibusConnectorClient and the original
	 * backendMessageId of the message.
	 * @throws Exception
	 */
	void deliverNewConfirmationFromConnectorClientToBackend(DomibusConnectorClientMessage newConfirmationMessage) throws Exception;

	/**
	 * This method is called by the domibusConnectorClient to push a new message to the backend application.
	 * 
	 * @param newMessage
	 * @throws Exception
	 */
	void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage newMessage) throws Exception;
}
