package eu.domibus.connector.client.controller.persistence.service;

import java.util.List;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface IDomibusConnectorClientPersistenceService {

	PDomibusConnectorClientMessage persistNewMessage(DomibusConnectorMessageType message, PDomibusConnectorClientMessageStatus status);

	PDomibusConnectorClientMessage persistAllConfirmaitonsForMessage(PDomibusConnectorClientMessage clientMessage,
			DomibusConnectorMessageType message);

	PDomibusConnectorClientConfirmation persistNewConfirmation(DomibusConnectorMessageConfirmationType confirmation,
			PDomibusConnectorClientMessage clientMessage);

	PDomibusConnectorClientMessage mergeClientMessage(PDomibusConnectorClientMessage clientMessage);

	PDomibusConnectorClientMessage findOriginalClientMessage(DomibusConnectorMessageType message);

//	List<PDomibusConnectorClientMessage> findUnconfirmedMessages();
	
	PDomibusConnectorClientMessageDao getMessageDao();
	
	void deleteMessage(PDomibusConnectorClientMessage clientMessage);

}