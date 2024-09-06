/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.exception.DomibusConnectorClientException;

/**
 * This interface must be implemented if the client is set up in push/push mode. All messages
 * delivered by the domibusConnector will be delivered to the client side using this interface.
 *
 * @author Bernhard Rieder
 */
public interface DomibusConnectorDeliveryClient {
    /**
     * This method must be implemented if the client is set up in push/push mode. All new business
     * messages delivered by the domibusConnector will be delivered to the client side using this
     * method. The message contains a {@link DomibusConnectorMessageDetailsType} and a
     * {@link DomibusConnectorMessageContentType} at least. Before delivered, the
     * {@link DomibusConnectorClientMessageHandler} is called.
     *
     * @param message - The business message delivered by the domibusConnector.
     * @throws DomibusConnectorClientException - If there is an error during the operation.
     */
    void receiveDeliveredMessageFromConnector(DomibusConnectorMessageType message)
        throws DomibusConnectorClientException;

    /**
     * This method must be implemented if the client is set up in push/push mode. All new
     * confirmations of messages delivered by the domibusConnector will be delivered to the client
     * side using this method. The message contains a {@link DomibusConnectorMessageDetailsType} and
     * one {@link DomibusConnectorMessageConfirmationType} at least. The ebms message id of the
     * original message this confirmation refers to is contained in the attribute refToMessageId of
     * the {@link DomibusConnectorMessageDetailsType}.
     *
     * @param message - The confirmation message delivered by the domibusConnector.
     * @throws DomibusConnectorClientException - If there is an error during the operation.
     */
    void receiveDeliveredConfirmationMessageFromConnector(
        DomibusConnectorMessageType message) throws DomibusConnectorClientException;
}
