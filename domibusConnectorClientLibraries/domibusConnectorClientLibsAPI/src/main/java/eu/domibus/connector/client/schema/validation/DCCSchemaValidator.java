package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface that may be implemented to validate a messages' business content against a schema.
 * Is extended by {@link eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator} and 
 * {@link eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator}
 * 
 * @author riederb
 *
 */
public interface DCCSchemaValidator {

	/**
	 * Method to validate the business content XML.
	 * 
	 * @param message - The message object holding the business content XML at message/MessageContent/contentXML
	 * 
	 * @return a ValidationResult holding single results.
	 */
	ValidationResult validateBusinessContentXML(DomibusConnectorMessageType message);
}
