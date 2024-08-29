/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DCCContentMappingException;
import eu.domibus.connector.client.exception.DCCMessageValidationException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface to prepare a messages' business content XML before submitting/delivering it.
 *
 * @author riederb
 */
public interface DomibusConnectorClientMessageHandler {
    /**
     * Method to prepare a messages' business content XML to be delivered to the backend. First, the
     * implementation of
     * {@link eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator} is
     * called if present. Then, the message is mapped calling the implementation of
     * {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present.
     * Last, the message gets again validated with an implementation of
     * {@link eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator} if present.
     *
     * @param message - The message object holding the business content XML at
     *                message/MessageContent/contentXML
     * @throws DCCMessageValidationException if there is an error during message validation
     * @throws DCCContentMappingException    if there is an error during content mapping
     */
    void prepareInboundMessage(DomibusConnectorMessageType message)
        throws DCCMessageValidationException, DCCContentMappingException;

    /**
     * Method to prepare a messages' business content XML to be submitted to the domibusConnector.
     * First, the implementation of
     * {@link eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator} is
     * called if present. Then, the message is mapped calling the implementation of
     * {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present.
     * Last, the message gets again validated with an implementation of
     * {@link eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator} if present.
     *
     * @param message - The message object holding the business content XML at
     *                message/MessageContent/contentXML
     * @throws DCCMessageValidationException if there is an error during message validation
     * @throws DCCContentMappingException    if there is an error during content mapping
     */
    void prepareOutboundMessage(DomibusConnectorMessageType message)
        throws DCCMessageValidationException, DCCContentMappingException;
}
