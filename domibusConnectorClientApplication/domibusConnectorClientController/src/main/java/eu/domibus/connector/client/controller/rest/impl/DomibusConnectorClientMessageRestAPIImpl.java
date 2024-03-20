package eu.domibus.connector.client.controller.rest.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.util.DomibusConnectorClientRestUtil;
import eu.domibus.connector.client.rest.DomibusConnectorClientMessageRestAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@RestController
@RequestMapping(DomibusConnectorClientMessageRestAPI.MESSAGERESTSERVICE_PATH)
public class DomibusConnectorClientMessageRestAPIImpl implements DomibusConnectorClientMessageRestAPI {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientMessageRestAPIImpl.class);

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientRestUtil util;

	@Override
	public DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient() {

		LOGGER.debug("#requestNewMessagesFromConnectorClient called");

		List<PDomibusConnectorClientMessage> receivedMessages = persistenceService.getMessageDao().findReceived();

		if(receivedMessages!=null && receivedMessages.size()>0) {
			DomibusConnectorClientMessageList clientMessages = util.mapMessagesFromModel(receivedMessages, true);

			receivedMessages.forEach(message -> {

				message.setMessageStatus(PDomibusConnectorClientMessageStatus.DELIVERED_BACKEND);
				persistenceService.mergeClientMessage(message);
			});

			LOGGER.debug("#requestNewMessagesFromConnectorClient returns {} messages.",clientMessages.getMessages().size());

			return clientMessages;
		}else {
			LOGGER.debug("#requestNewMessagesFromConnectorClient: no new messages to return.");
		}
		return null;
	}

	@Override
	public DomibusConnectorClientMessageList requestRejectedOrConfirmedMessagesFromConnectorClient() {

		LOGGER.debug("#requestRejectedOrConfirmedMessagesFromConnectorClient called");

		List<PDomibusConnectorClientMessage> rejectedOrConfirmedMessages = persistenceService.getMessageDao().findRejectedConfirmed();

		if(rejectedOrConfirmedMessages!=null && rejectedOrConfirmedMessages.size()>0) {
			DomibusConnectorClientMessageList clientMessages = util.mapMessagesFromModel(rejectedOrConfirmedMessages, true);

			rejectedOrConfirmedMessages.forEach(message -> {

				message.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_DELIVERED_BACKEND);
				persistenceService.mergeClientMessage(message);
			});

			LOGGER.debug("#requestRejectedOrConfirmedMessagesFromConnectorClient returns {} messages.",clientMessages.getMessages().size());
			return clientMessages;
		}else {
			LOGGER.debug("#requestRejectedOrConfirmedMessagesFromConnectorClient: no new rejected or confirmed messages to return.");
		}
		return null;
	}

}
