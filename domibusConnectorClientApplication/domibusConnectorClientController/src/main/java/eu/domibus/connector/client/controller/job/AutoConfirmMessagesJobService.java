package eu.domibus.connector.client.controller.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@Validated
@Valid
public class AutoConfirmMessagesJobService {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AutoConfirmMessagesJobService.class);

	@Autowired
	@NotNull
	private DomibusConnectorClientBackend clientBackend;

	@Autowired
	@NotNull
	private IDomibusConnectorClientPersistenceService persistenceService;
	
	@Autowired
	@NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;

	public void autoConfirmMessages() {
		LocalDateTime startTime = LocalDateTime.now();
		LOGGER.debug("AutoConfirmMessagesJobService started");
		
		List<PDomibusConnectorClientMessage> unconfirmedMessages = persistenceService.getMessageDao().findReceived();

		unconfirmedMessages.forEach(message -> {
			DomibusConnectorConfirmationType type = null;
			if(message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED)) {
				type = DomibusConnectorConfirmationType.DELIVERY;
			}else {
				type = DomibusConnectorConfirmationType.NON_DELIVERY;
			}
			DomibusConnectorMessageType originalMessage = messageBuilder.createNewMessage(
					message.getBackendMessageId(), 
					message.getEbmsMessageId(), 
					message.getConversationId(), 
					message.getService(),
					message.getServiceType(),
					message.getAction(), 
					message.getFromPartyId(), 
					message.getFromPartyType(), 
					message.getFromPartyRole(), 
					message.getToPartyId(), 
					message.getToPartyType(), 
					message.getToPartyRole(), 
					message.getFinalRecipient(), 
					message.getOriginalSender());
		      try {
		    	clientBackend.triggerConfirmationForMessage(originalMessage, type, null);
//		    	DomibusConnectorMessageConfirmationType confirmationType = new DomibusConnectorMessageConfirmationType();
//		    	confirmationType.setConfirmationType(type);
//				PDomibusConnectorClientConfirmation clientConfirmation = persistenceService.persistNewConfirmation(confirmationType , message);
//		    	message.getConfirmations().add(clientConfirmation);
//		    	persistenceService.mergeClientMessage(message);
			} catch (DomibusConnectorClientBackendException e) {
				LOGGER.error("Exception occured triggering the confirmation for message at the client backend", e);
			}
		      message.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_TRIGGERED);
		      persistenceService.mergeClientMessage(message);
		});
		
		LOGGER.debug("AutoConfirmMessagesJobService finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
	}

}
