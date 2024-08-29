/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.schema.validation;

/**
 * Interface that may be implemented to validate a messages' business content XML against a local
 * schema. If an implementation is present, it is called when a message is prepared to be submitted
 * to the domibusConnector, or delivered to the backend using the
 * {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler}.
 *
 * @author riederb
 */
public interface DCCLocalSchemaValidator extends DCCSchemaValidator {
}
