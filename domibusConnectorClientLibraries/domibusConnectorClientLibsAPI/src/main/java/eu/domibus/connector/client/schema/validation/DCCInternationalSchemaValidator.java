package eu.domibus.connector.client.schema.validation;

/**
 * Interface that may be implemented to validate a messages' business content XML against an international schema.
 * If an implementation is present, it is called when a message is prepared to be submitted to the domibusConnector,
 * or delivered to the backend using the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler}.
 * 
 * @author riederb
 *
 */
public interface DCCInternationalSchemaValidator extends DCCSchemaValidator {
	
	
}
