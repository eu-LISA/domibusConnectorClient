package eu.domibus.connector.client.controller.persistence.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientConfirmationDao;
import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;

@Component
public class DomibusConnectorClientPersistenceService implements IDomibusConnectorClientPersistenceService {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientPersistenceService.class);

	@Autowired
	private PDomibusConnectorClientMessageDao messageDao;
	
	@Autowired
	private PDomibusConnectorClientConfirmationDao confirmationDao;

	public DomibusConnectorClientPersistenceService() {}

	@Override
	public PDomibusConnectorClientMessage persistNewMessage(DomibusConnectorMessageType message, PDomibusConnectorClientMessageStatus status) {
		if(message!=null) {
			PDomibusConnectorClientMessage newMessage = new PDomibusConnectorClientMessage();

			DomibusConnectorMessageDetailsType messageDetails = message.getMessageDetails();
			if(messageDetails!=null) {
				newMessage.setBackendMessageId(messageDetails.getBackendMessageId());
				newMessage.setConversationId(messageDetails.getConversationId());
				newMessage.setEbmsMessageId(messageDetails.getEbmsMessageId());
				newMessage.setFinalRecipient(messageDetails.getFinalRecipient());
				newMessage.setOriginalSender(messageDetails.getOriginalSender());
				if(messageDetails.getAction()!=null) {
					newMessage.setAction(messageDetails.getAction().getAction());
				}
				if(messageDetails.getService()!=null) {
					newMessage.setService(messageDetails.getService().getService());
					newMessage.setServiceType(messageDetails.getService().getServiceType());
				}

				DomibusConnectorPartyType fromParty = messageDetails.getFromParty();
				if(fromParty!=null) {
					newMessage.setFromPartyId(fromParty.getPartyId());
					newMessage.setFromPartyRole(fromParty.getRole());
					newMessage.setFromPartyType(fromParty.getPartyIdType());
				}

				DomibusConnectorPartyType toParty = messageDetails.getToParty();
				if(toParty!=null) {
					newMessage.setToPartyId(toParty.getPartyId());
					newMessage.setToPartyRole(toParty.getRole());
					newMessage.setToPartyType(toParty.getPartyIdType());
				}
			}
			newMessage.setCreated(new Date());
			newMessage.setMessageStatus(status);
			
			newMessage = messageDao.save(newMessage);

			return newMessage;
		}
		return null;
	}
	
	@Override
	public PDomibusConnectorClientMessage persistAllConfirmaitonsForMessage(PDomibusConnectorClientMessage clientMessage, DomibusConnectorMessageType message) {
		List<DomibusConnectorMessageConfirmationType> messageConfirmations = message.getMessageConfirmations();
		if(messageConfirmations!=null && !messageConfirmations.isEmpty()) {
			messageConfirmations.forEach(messageConfirmation -> {
				PDomibusConnectorClientConfirmation clientConfirmaiton = persistNewConfirmation(messageConfirmation, clientMessage);
				clientMessage.getConfirmations().add(clientConfirmaiton);
				clientMessage.setLastConfirmationReceived(clientConfirmaiton.getConfirmationType());
			});
			messageDao.save(clientMessage);
		}
		return clientMessage;
	}
	
	@Override
	public PDomibusConnectorClientConfirmation persistNewConfirmation(DomibusConnectorMessageConfirmationType confirmation, PDomibusConnectorClientMessage clientMessage) {
		PDomibusConnectorClientConfirmation clientConfirmation = new PDomibusConnectorClientConfirmation();
		clientConfirmation.setMessage(clientMessage);
		clientConfirmation.setConfirmationType(confirmation.getConfirmationType().name());
		clientConfirmation.setReceived(new Date());

		clientConfirmation = confirmationDao.save(clientConfirmation);
		
		return clientConfirmation;
	}
	
	@Override
	public PDomibusConnectorClientMessage mergeClientMessage(PDomibusConnectorClientMessage clientMessage) {
		
		clientMessage.getConfirmations().forEach(confirmation -> {
			confirmationDao.save(confirmation);
		});
		clientMessage = messageDao.save(clientMessage);
		
		return clientMessage;
	}
	
	
	@Override
	public PDomibusConnectorClientMessage findOriginalClientMessage(DomibusConnectorMessageType message) {
		DomibusConnectorMessageDetailsType messageDetails = message.getMessageDetails();
		if(messageDetails!=null) {
			
			if(messageDetails.getRefToMessageId()!=null) {
				LOGGER.debug("#findOriginalClientMessage: try with refToMessageId {}", messageDetails.getRefToMessageId());
				Optional<PDomibusConnectorClientMessage> clientMessage = messageDao.findOneByEbmsMessageId(messageDetails.getRefToMessageId());
				if(clientMessage.isPresent()) {
					LOGGER.debug("#findOriginalClientMessage: original message found by ebmsId");
					return clientMessage.get();
				}else {
					clientMessage = messageDao.findOneByBackendMessageId(messageDetails.getRefToMessageId());
					if(clientMessage.isPresent()) {
						LOGGER.debug("#findOriginalClientMessage: original message found by backendMessageId");
						return clientMessage.get();
					}
				}
			}
			
			if(messageDetails.getBackendMessageId()!=null) {
				LOGGER.debug("#findOriginalClientMessage: try with backendMessageId {}", messageDetails.getBackendMessageId());
				Optional<PDomibusConnectorClientMessage> clientMessage = messageDao.findOneByBackendMessageId(messageDetails.getBackendMessageId());
				if(clientMessage.isPresent()) {
					LOGGER.debug("#findOriginalClientMessage: original message found by backendMessageId");
					return clientMessage.get();
				}
			}
			
		}
		
		return null;
	}
	
	@Override
	public PDomibusConnectorClientMessageDao getMessageDao() {
		return messageDao;
	}

	@Override
	public void deleteMessage(PDomibusConnectorClientMessage clientMessage) {
		LOGGER.debug("#deleteMessage: called");
		if(!clientMessage.getConfirmations().isEmpty()) {
			for(PDomibusConnectorClientConfirmation confirmation:clientMessage.getConfirmations()) {
				this.confirmationDao.delete(confirmation);
			}
		}
		
		this.messageDao.delete(clientMessage);
		LOGGER.debug("#deleteMessage: success");
	}
	

}
