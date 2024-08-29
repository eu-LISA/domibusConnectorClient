/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.link.ws.impl;

import eu.domibus.connector.client.DomibusConnectorDeliveryClient;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * The {@code DomibusConnectorClientDeliveryWsImpl} class is an implementation of the
 * {@code DomibusConnectorBackendDeliveryWebService} interface. It handles the delivery of messages
 * and confirmation messages from the domibusConnector to the client.
 *
 * <p>This class has a dependency on the {@code DomibusConnectorDeliveryClient} interface, which
 * must be implemented if the client is set up in push/push mode.
 *
 * @see DomibusConnectorBackendDeliveryWebService
 * @see DomibusConnectorDeliveryClient
 * @see DomibusConnectorMessageType
 * @see DomibsConnectorAcknowledgementType
 */
public class DomibusConnectorClientDeliveryWsImpl
    implements DomibusConnectorBackendDeliveryWebService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientDeliveryWsImpl.class);
    @Autowired
    private DomibusConnectorDeliveryClient deliveryClient;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType msg) {

        LOGGER.info("#deliverMessage: called");
        var ackResponse = new DomibsConnectorAcknowledgementType();

        if (msg.getMessageContent() != null) {
            LOGGER.debug("#deliverMessage: received message is a business message...");
            try {
                deliveryClient.receiveDeliveredMessageFromConnector(msg);
                ackResponse.setResultMessage("Message successfully delivered to client.");
                ackResponse.setResult(true);
            } catch (Exception e) {
                LOGGER.error(
                    "Exception occured while delivering message from connector to client!", e);
                ackResponse.setResultMessage(e.getMessage());
                ackResponse.setResult(false);
            }
        } else if (!CollectionUtils.isEmpty(msg.getMessageConfirmations())) {
            // as there is no message content, but at least one message confirmation,
            // it is a confirmation message
            LOGGER.debug("#deliverMessage: received message is a confirmation message...");
            try {
                deliveryClient.receiveDeliveredConfirmationMessageFromConnector(msg);
                ackResponse.setResultMessage(
                    "Confirmation message successfully delivered to client.");
                ackResponse.setResult(true);
            } catch (Exception e) {
                LOGGER.error(
                    "Exception occured while delivering confirmation message from connector to "
                        + "client!",
                    e
                );
                ackResponse.setResultMessage(e.getMessage());
                ackResponse.setResult(false);
            }
        }

        return ackResponse;
    }
}
