/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.persistence.service;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;

/**
 * The IDomibusConnectorClientPersistenceService interface provides methods for persisting and
 * retrieving messages and confirmations sent by a Domibus connector client.
 */
public interface IDomibusConnectorClientPersistenceService {
    PDomibusConnectorClientMessage persistNewMessage(
        DomibusConnectorMessageType message, PDomibusConnectorClientMessageStatus status);

    PDomibusConnectorClientMessage persistAllConfirmationsForMessage(
        PDomibusConnectorClientMessage clientMessage,
        DomibusConnectorMessageType message);

    PDomibusConnectorClientConfirmation persistNewConfirmation(
        DomibusConnectorMessageConfirmationType confirmation,
        PDomibusConnectorClientMessage clientMessage);

    PDomibusConnectorClientMessage mergeClientMessage(PDomibusConnectorClientMessage clientMessage);

    PDomibusConnectorClientMessage findOriginalClientMessage(DomibusConnectorMessageType message);

    PDomibusConnectorClientMessageDao getMessageDao();

    void deleteMessage(PDomibusConnectorClientMessage clientMessage);
}
