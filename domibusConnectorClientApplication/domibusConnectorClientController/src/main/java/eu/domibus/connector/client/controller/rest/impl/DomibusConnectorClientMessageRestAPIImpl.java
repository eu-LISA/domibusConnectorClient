/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.rest.impl;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.util.DomibusConnectorClientRestUtil;
import eu.domibus.connector.client.rest.DomibusConnectorClientMessageRestAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class implements the {@link DomibusConnectorClientMessageRestAPI} interface and provides
 * RESTful endpoints to request messages and confirmations from the Domibus Connector Client.
 */
@RestController
@RequestMapping(DomibusConnectorClientMessageRestAPI.MESSAGERESTSERVICE_PATH)
public class DomibusConnectorClientMessageRestAPIImpl
    implements DomibusConnectorClientMessageRestAPI {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientMessageRestAPIImpl.class);
    @Autowired
    private IDomibusConnectorClientPersistenceService persistenceService;
    @Autowired
    private DomibusConnectorClientRestUtil util;

    @Override
    public DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient() {
        LOGGER.debug("#requestNewMessagesFromConnectorClient called");

        List<PDomibusConnectorClientMessage> receivedMessages =
            persistenceService.getMessageDao().findReceived();

        if (receivedMessages != null && !receivedMessages.isEmpty()) {
            DomibusConnectorClientMessageList clientMessages =
                util.mapMessagesFromModel(receivedMessages, true);

            receivedMessages.forEach(message -> {

                message.setMessageStatus(PDomibusConnectorClientMessageStatus.DELIVERED_BACKEND);
                persistenceService.mergeClientMessage(message);
            });

            LOGGER.debug(
                "#requestNewMessagesFromConnectorClient returns {} messages.",
                clientMessages.getMessages().size()
            );

            return clientMessages;
        } else {
            LOGGER.debug("#requestNewMessagesFromConnectorClient: no new messages to return.");
        }
        return null;
    }

    @Override
    public DomibusConnectorClientMessageList
    requestRejectedOrConfirmedMessagesFromConnectorClient() {
        LOGGER.debug("#requestRejectedOrConfirmedMessagesFromConnectorClient called");

        List<PDomibusConnectorClientMessage> rejectedOrConfirmedMessages =
            persistenceService.getMessageDao().findRejectedConfirmed();

        if (rejectedOrConfirmedMessages != null && !rejectedOrConfirmedMessages.isEmpty()) {
            DomibusConnectorClientMessageList clientMessages =
                util.mapMessagesFromModel(rejectedOrConfirmedMessages, true);

            rejectedOrConfirmedMessages.forEach(message -> {

                message.setMessageStatus(
                    PDomibusConnectorClientMessageStatus.CONFIRMATION_DELIVERED_BACKEND);
                persistenceService.mergeClientMessage(message);
            });

            LOGGER.debug(
                "#requestRejectedOrConfirmedMessagesFromConnectorClient returns {} messages.",
                clientMessages.getMessages().size()
            );
            return clientMessages;
        } else {
            LOGGER.debug(
                "#requestRejectedOrConfirmedMessagesFromConnectorClient: no new rejected or "
                    + "confirmed messages to return."
            );
        }
        return null;
    }
}
