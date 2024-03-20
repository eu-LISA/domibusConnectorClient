package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This interface must be implemented if the domibusConnectorClientScheduler is used and/or the client is set up in push/pull mode!
 * 
 * @author Bernhard Rieder
 *
 */
public interface DomibusConnectorClientBackend {

	/**
	 * This method asks the backend of the client if new messages are to submit to the connector.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client implementation is not self aware to 
	 * recognize new messages at its backend.
	 *  
	 * @return messages object holding a Collection of messages.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward messages received.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client is set up in push/pull mode.
	 * 
	 * @param message - The message object received from the connector.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward messages received.
	 * Must be implemented if the message pulling with acknowledgement is used.
	 * 
	 * @param message - The message object received from the connector.
	 * @param messageTransportId - The transport ID the connector gives a message.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewAcknowledgeableMessageToClientBackend(DomibusConnectorMessageType message, String messageTransportId) throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward confirmation received.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client is set up in push/pull mode.
	 * 
	 * @param message - The message object containing the confirmation received from the connector.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward confirmation received.
	 * Must be implemented if the message pulling with acknowledgement is used.
	 * 
	 * @param message - The message object containing the confirmation received from the connector.
	 * @param messageTransportId - The transport ID the connector gives a message.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewAcknowledgeableConfirmationToClientBackend(DomibusConnectorMessageType message, String messageTransportId) throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the connector to generate and send a confirmation.
	 * 
	 * @param originalMessage - The original message the confirmation should be triggered for.
	 * @param confirmationType - The type of confirmation that should be triggered.
	 * @param confirmationAction - The use-case specific action that is used for transmission of the confirmation
	 * @throws DomibusConnectorClientBackendException
	 */
	void triggerConfirmationForMessage(DomibusConnectorMessageType originalMessage,
			DomibusConnectorConfirmationType confirmationType, String confirmationAction)
			throws DomibusConnectorClientBackendException;

	
}
