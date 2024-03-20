package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * This interface must be implemented if the client is set up in push/push mode.
 * All messages delivered by the domibusConnector will be delivered to the client side using this interface.
 * 
 * @author Bernhard Rieder
 *
 */
public interface DomibusConnectorDeliveryClient {

	
	/**
	 * This method must be implemented if the client is set up in push/push mode.
	 * All new business messages delivered by the domibusConnector will be delivered to the client side using this method.
	 * The message contains a {@link DomibusConnectorMessageDetailsType} and a {@link DomibusConnectorMessageContentType} at least.
	 * Before delivered, the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler} is called.
	 * 
	 * @param message - The business message delivered by the domibusConnector.
	 * @throws DomibusConnectorClientException 
	 */
	public void receiveDeliveredMessageFromConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException;
	
	/**
	 * This method must be implemented if the client is set up in push/push mode.
	 * All new confirmations of messages delivered by the domibusConnector will be delivered to the client side using this method.
	 * The message contains a {@link DomibusConnectorMessageDetailsType} and one {@link DomibusConnectorMessageConfirmationType} at least.
	 * The ebms message id of the original message this confirmation refers to is contained in the attribute refToMessageId of the {@link DomibusConnectorMessageDetailsType}.
	 * 
	 * @param message - The confirmation message delivered by the domibusConnector.
	 */
	public void receiveDeliveredConfirmationMessageFromConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException;
	
}
