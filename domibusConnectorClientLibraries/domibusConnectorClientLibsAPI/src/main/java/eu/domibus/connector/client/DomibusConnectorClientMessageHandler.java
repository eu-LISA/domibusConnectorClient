package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DCCContentMappingException;
import eu.domibus.connector.client.exception.DCCMessageValidationException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface to prepare a messages' business content XML before submitting/delivering it.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientMessageHandler {

	
	/**
	 * Method to prepare a messages' business content XML to be delivered to the backend.
	 * First, the implementation of {@link eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator} is called if present.
	 * Then, the message is mapped calling the implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present. 
	 * Last, the message gets again validated with an implementation of {@link eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator} if present.
	 * 
	 * @param message - The message object holding the business content XML at message/MessageContent/contentXML
	 * @throws DCCContentMappingException 
	 * @throws DCCMessageValidationException 
	 */
	void prepareInboundMessage(DomibusConnectorMessageType message)
			throws DCCMessageValidationException, DCCContentMappingException;
	
	/**
	 * Method to prepare a messages' business content XML to be submitted to the domibusConnector.
	 * First, the implementation of {@link eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator} is called if present.
	 * Then, the message is mapped calling the implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present. 
	 * Last, the message gets again validated with an implementation of {@link eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator} if present.
	 * 
	 * @param message - The message object holding the business content XML at message/MessageContent/contentXML
	 * @throws DCCContentMappingException 
	 * @throws DCCMessageValidationException 
	 */
	void prepareOutboundMessage(DomibusConnectorMessageType message)
			throws DCCMessageValidationException, DCCContentMappingException;

}