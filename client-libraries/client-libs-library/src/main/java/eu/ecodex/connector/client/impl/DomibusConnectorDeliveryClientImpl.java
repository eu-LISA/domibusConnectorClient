/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.impl;

import eu.ecodex.connector.client.DomibusConnectorClientBackend;
import eu.ecodex.connector.client.DomibusConnectorClientMessageHandler;
import eu.ecodex.connector.client.DomibusConnectorDeliveryClient;
import eu.ecodex.connector.client.exception.DomibusConnectorClientBackendException;
import eu.ecodex.connector.client.exception.DomibusConnectorClientException;
import eu.ecodex.connector.client.link.ws.configuration.ConnectorLinkWSProperties;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link DomibusConnectorDeliveryClient} interface. This class is responsible
 * for receiving messages and confirmations from the Domibus Connector in push/push mode. It handles
 * the received messages by invoking the {@link DomibusConnectorClientMessageHandler} and delivers
 * them to the client backend by calling the {@link DomibusConnectorClientBackend}. It also handles
 * the received message confirmations by delivering them to the client backend.
 *
 * @see DomibusConnectorDeliveryClient
 * @see DomibusConnectorClientMessageHandler
 * @see DomibusConnectorClientBackend
 */
@Component
@ConditionalOnProperty(
    prefix = ConnectorLinkWSProperties.PREFIX,
    value = ConnectorLinkWSProperties.PUSH_ENABLED_PROPERTY_NAME, matchIfMissing = false
)
@NoArgsConstructor
public class DomibusConnectorDeliveryClientImpl implements DomibusConnectorDeliveryClient {
    private static final Logger LOGGER =
        LogManager.getLogger(DomibusConnectorDeliveryClientImpl.class);
    @Autowired
    private DomibusConnectorClientMessageHandler messageHandler;
    @Autowired
    private DomibusConnectorClientBackend clientBackend;

    @Override
    public void receiveDeliveredMessageFromConnector(DomibusConnectorMessageType message)
        throws DomibusConnectorClientException {
        LOGGER.info(
            "#receiveDeliveredMessageFromConnector: received new message from connector via push.");
        messageHandler.prepareInboundMessage(message);

        try {
            clientBackend.deliverNewMessageToClientBackend(message);
        } catch (DomibusConnectorClientBackendException e) {
            throw new DomibusConnectorClientException(e);
        }
        LOGGER.debug(
            "receiveDeliveredMessageFromConnector: received message delivered to client backend.");
    }

    @Override
    public void receiveDeliveredConfirmationMessageFromConnector(
        DomibusConnectorMessageType message)
        throws DomibusConnectorClientException {
        LOGGER.info(
            "#receiveDeliveredConfirmationMessageFromConnector: received new confirmation from "
                + "connector via push.");
        try {
            clientBackend.deliverNewConfirmationToClientBackend(message);
        } catch (DomibusConnectorClientBackendException e) {
            throw new DomibusConnectorClientException(e);
        }
        LOGGER.debug(
            "receiveDeliveredConfirmationMessageFromConnector: received confirmation delivered "
                + "to client backend.");
    }
}
