/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.link.ws.impl;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import eu.domibus.connector.ws.backend.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.backend.webservice.ListPendingMessageIdsResponse;
import eu.ecodex.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.ecodex.connector.client.link.DomibusConnectorClientLink;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The `DomibusConnectorClientWSLinkImpl` class implements the `DomibusConnectorClientLink`
 * interface and serves as an abstraction layer between the `domibusConnectorClientWSLink ` module
 * and the `domibusConnectorClientLibrary`. It delegates methods calls through the WSLink to the
 * `domibusConnectorAPI` backend.
 */
public class DomibusConnectorClientWSLinkImpl implements DomibusConnectorClientLink {
    private static final Logger LOGGER =
        LogManager.getLogger(DomibusConnectorClientWSLinkImpl.class);
    @Autowired
    DomibusConnectorBackendWebService connectorWsClient;

    @Override
    public DomibusConnectorMessagesType requestMessagesFromConnector()
        throws DomibusConnectorBackendWebServiceClientException {
        try {
            return connectorWsClient.requestMessages(new EmptyRequestType());
        } catch (Exception e) {
            LOGGER.error("Exception while requesting messages from Connector!");
            throw new DomibusConnectorBackendWebServiceClientException(
                "Exception while requesting messages from Connector!", e);
        }
    }

    @Override
    public DomibsConnectorAcknowledgementType submitMessageToConnector(
        DomibusConnectorMessageType message)
        throws DomibusConnectorBackendWebServiceClientException {
        try {
            return connectorWsClient.submitMessage(message);
        } catch (Exception e) {
            LOGGER.error(
                "Exception while submitting message with backendId {} to Connector!",
                message.getMessageDetails().getBackendMessageId()
            );
            throw new DomibusConnectorBackendWebServiceClientException(
                "Exception while submitting message to Connector!", e);
        }
    }

    @Override
    public List<String> listPendingMessages()
        throws DomibusConnectorBackendWebServiceClientException {
        try {
            ListPendingMessageIdsResponse pendingMessagesIds =
                connectorWsClient.listPendingMessageIds(new EmptyRequestType());
            return pendingMessagesIds.getMessageTransportIds();
        } catch (Exception e) {
            LOGGER.error(
                "Exception while calling webservice method 'listPendingMessageIds' at connector!");
            throw new DomibusConnectorBackendWebServiceClientException(
                "Exception while calling webservice method 'listPendingMessageIds' at connector!",
                e
            );
        }
    }

    @Override
    public DomibusConnectorMessageType getMessageById(String messageTransportId)
        throws DomibusConnectorBackendWebServiceClientException {
        try {
            var request = new GetMessageByIdRequest();
            request.setMessageTransportId(messageTransportId);
            DomibusConnectorMessageType messageById = connectorWsClient.getMessageById(request);
            if (messageById != null) {
                return messageById;
            } else {
                throw new DomibusConnectorBackendWebServiceClientException(
                    "Connector returned null when message with messageTransportId "
                        + messageTransportId + " was requested!");
            }
        } catch (Exception e) {
            LOGGER.error(
                "Exception while requesting message with messageTransportId {} from connector!",
                messageTransportId
            );
            throw new DomibusConnectorBackendWebServiceClientException(
                "Exception while requesting message with messageTransportId " + messageTransportId
                    + " from connector!", e);
        }
    }

    @Override
    public void acknowledgeMessage(DomibusConnectorMessageResponseType result)
        throws DomibusConnectorBackendWebServiceClientException {
        try {
            connectorWsClient.acknowledgeMessage(result);
        } catch (Exception e) {
            LOGGER.error(
                "Exception while send acknowledge of message with messageTransportId {} "
                    + "to connector!",
                result.getResponseForMessageId()
            );
            throw new DomibusConnectorBackendWebServiceClientException(
                "Exception while send acknowledge of message with messageTransportId "
                    + result.getResponseForMessageId() + " to connector!", e);
        }
    }
}
