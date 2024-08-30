/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.rest;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;

/**
 * This interface should be used if a backend application should receive messages via push. In this
 * case the backend application must implement a REST service that implements this interface.
 * Certain properties have to be set properly that the domibusConnectorClient can initialize a REST
 * client that also implements this interface.
 *
 * @author riederb
 */
public interface DomibusConnectorClientDeliveryRestClientAPI {
    /**
     * This method is called by the domibusConnectorClient to push a new confirmation message to the
     * backend application.
     *
     * @param newConfirmationMessage a client message object holding the new confirmation received
     *                               by the domibusConnectorClient and the original backendMessageId
     *                               of the message.
     * @throws Exception if an error occurs during the delivery process
     */
    void deliverNewConfirmationFromConnectorClientToBackend(
        DomibusConnectorClientMessage newConfirmationMessage) throws Exception;

    /**
     * This method is called by the domibusConnectorClient to push a new message to the backend
     * application.
     *
     * @param newMessage The new message to be delivered, represented by an instance of
     *                   {@link DomibusConnectorClientMessage}.
     * @throws Exception if an error occurs during the delivery process.
     */
    void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage newMessage)
        throws Exception;
}
