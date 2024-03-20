package eu.domibus.connector.client.controller.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.DomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class DatabaseRestore {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DatabaseRestore.class);

	private DomibusConnectorClientStorage storage;
	private DomibusConnectorClientPersistenceService persistenceService;

	public DatabaseRestore() {
		// TODO Auto-generated constructor stub
	}

	public void restoreDatabaseFromStorage() {
		LOGGER.debug("#restoreDatabaseFromStorage: enter");

		if(getStorage()!=null) {
			LOGGER.debug("Storage set!");
		}

		if(getPersistenceService()!=null) {
			LOGGER.debug("PersistenceService set!");
		}

		Map<String, DomibusConnectorMessageType> allStoredMessages = storage.getAllStoredMessages();

		if(allStoredMessages!=null && !allStoredMessages.isEmpty()) {
			LOGGER.info("Found {} messages in storage to restore.", allStoredMessages.size());
			allStoredMessages.keySet().forEach(storageLocation -> {
				DomibusConnectorMessageType message = allStoredMessages.get(storageLocation);

				restoreMessageToDatabase(storageLocation, message);
			});
		}
	}

	public void restoreMessageToDatabase(String storageLocation, DomibusConnectorMessageType message) {
		LOGGER.debug("#restoreDatabaseFromStorage: enter");

		if(message == null || message.getMessageDetails() == null) {
			LOGGER.warn("Message from storageLocation {} cannot be restored as no message details are given!", storageLocation);
		}

		String ebmsId = message.getMessageDetails().getEbmsMessageId();
		String backendId = message.getMessageDetails().getBackendMessageId();

		if(!StringUtils.isEmpty(ebmsId) && !StringUtils.isEmpty(backendId)) {
			Optional<PDomibusConnectorClientMessage> finding = persistenceService.getMessageDao().findOneByEbmsMessageIdAndBackendMessageId(ebmsId, backendId);
			if(finding.isPresent()) {
				LOGGER.info("Found message with ebmsId {} and backendId {} in database.", ebmsId, backendId);
				PDomibusConnectorClientMessage preparedDatabaseEntry = prepareDatabaseEntry(finding.get(), message, storageLocation, PDomibusConnectorClientMessageStatus.RECEIVED);
				this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
				return;
			}
		}

		if(!StringUtils.isEmpty(ebmsId)) {
			Optional<PDomibusConnectorClientMessage> finding = persistenceService.getMessageDao().findOneByEbmsMessageId(ebmsId);
			if(finding.isPresent()) {
				PDomibusConnectorClientMessage possibleFinding = finding.get();
				if(possibleFinding.getStorageInfo().equals(storageLocation)) {
					LOGGER.info("Found message with ebmsId {} and storageLocation {} in database.", ebmsId, storageLocation);
					PDomibusConnectorClientMessage preparedDatabaseEntry = prepareDatabaseEntry(possibleFinding, message, storageLocation, PDomibusConnectorClientMessageStatus.RECEIVED);
					this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
					return;
				}
			}
		}

		if(!StringUtils.isEmpty(backendId)) {
			Optional<PDomibusConnectorClientMessage> finding = persistenceService.getMessageDao().findOneByBackendMessageId(backendId);
			if(finding.isPresent()) {
				PDomibusConnectorClientMessage possibleFinding = finding.get();
				if(possibleFinding.getStorageInfo().equals(storageLocation)) {
					LOGGER.info("Found message with backendId {} and storageLocation {} in database.", backendId, storageLocation);
					PDomibusConnectorClientMessage preparedDatabaseEntry = prepareDatabaseEntry(possibleFinding, message, storageLocation, PDomibusConnectorClientMessageStatus.PREPARED);
					this.persistenceService.mergeClientMessage(preparedDatabaseEntry);
					return;
				}
			}
		}

		LOGGER.info("Message from storageLocation {} not in database yet. Creating one!", storageLocation);

		PDomibusConnectorClientMessage preparedDatabaseEntry = prepareDatabaseEntry(new PDomibusConnectorClientMessage(), message, storageLocation, ebmsId!=null?PDomibusConnectorClientMessageStatus.RECEIVED:PDomibusConnectorClientMessageStatus.PREPARED);

		this.persistenceService.getMessageDao().save(preparedDatabaseEntry);

		preparedDatabaseEntry = handleConfirmations(preparedDatabaseEntry, message);

		this.persistenceService.getMessageDao().save(preparedDatabaseEntry);
	}

	private PDomibusConnectorClientMessage prepareDatabaseEntry(PDomibusConnectorClientMessage pMessage, DomibusConnectorMessageType message, String storageLocation, PDomibusConnectorClientMessageStatus messageStatus) {
		pMessage.setAction(message.getMessageDetails().getAction()!=null?message.getMessageDetails().getAction().getAction():null);
		pMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
		pMessage.setConversationId(message.getMessageDetails().getConversationId());
		pMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
		pMessage.setFinalRecipient(message.getMessageDetails().getFinalRecipient());
		if(message.getMessageDetails().getFromParty()!=null) {
			pMessage.setFromPartyId(message.getMessageDetails().getFromParty().getPartyId());
			pMessage.setFromPartyRole(message.getMessageDetails().getFromParty().getRole());
			pMessage.setFromPartyType(message.getMessageDetails().getFromParty().getPartyIdType());
		}
		pMessage.setOriginalSender(message.getMessageDetails().getOriginalSender());
		pMessage.setService(message.getMessageDetails().getService()!=null?message.getMessageDetails().getService().getService():null);
		pMessage.setServiceType(message.getMessageDetails().getService()!=null?message.getMessageDetails().getService().getServiceType():null);
		pMessage.setStorageInfo(storageLocation);
		pMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
		if(message.getMessageDetails().getToParty()!=null) {
			pMessage.setToPartyId(message.getMessageDetails().getToParty().getPartyId());
			pMessage.setToPartyRole(message.getMessageDetails().getToParty().getRole());
			pMessage.setToPartyType(message.getMessageDetails().getToParty().getPartyIdType());
		}
		pMessage.setMessageStatus(messageStatus);
		if(pMessage.getCreated()==null) {
			pMessage.setCreated(new Date());
		}


		return pMessage;
	}

	private PDomibusConnectorClientMessage handleConfirmations(PDomibusConnectorClientMessage pMessage, DomibusConnectorMessageType message) {
		if(!CollectionUtils.isEmpty(message.getMessageConfirmations())) {
			List<DomibusConnectorMessageConfirmationType> messageConfirmaitons = new ArrayList<DomibusConnectorMessageConfirmationType>(message.getMessageConfirmations());
			//			Collections.copy(messageConfirmaitons, message.getMessageConfirmations());
			if(!CollectionUtils.isEmpty(pMessage.getConfirmations())) {
				pMessage.getConfirmations().forEach(confirmation -> {
					String type = confirmation.getConfirmationType();

					message.getMessageConfirmations().forEach(msgConf -> {
						if(msgConf.getConfirmationType().name().equals(type)) {
							pMessage.setMessageStatus(confirmationTypeToMessageStatus(msgConf.getConfirmationType(), pMessage.getMessageStatus(), message.getMessageDetails().getEbmsMessageId()));
							messageConfirmaitons.remove(msgConf);
						}
					});
				});
			}

			if(!messageConfirmaitons.isEmpty()) {
				messageConfirmaitons.forEach(conf -> {
					PDomibusConnectorClientConfirmation clientConf = this.persistenceService.persistNewConfirmation(conf, pMessage);
					pMessage.getConfirmations().add(clientConf);
					pMessage.setMessageStatus(confirmationTypeToMessageStatus(conf.getConfirmationType(), pMessage.getMessageStatus(), message.getMessageDetails().getEbmsMessageId()));
				});
			}
		}

		return pMessage;
	}

	private PDomibusConnectorClientMessageStatus confirmationTypeToMessageStatus(DomibusConnectorConfirmationType type, PDomibusConnectorClientMessageStatus messageStatus, String ebmsId) {
		switch (type) {
		case RETRIEVAL:
		case DELIVERY: return PDomibusConnectorClientMessageStatus.CONFIRMED;
		case NON_DELIVERY:
		case SUBMISSION_REJECTION:
		case RELAY_REMMD_FAILURE:
		case NON_RETRIEVAL:
		case RELAY_REMMD_REJECTION: 
			return PDomibusConnectorClientMessageStatus.REJECTED;
		case RELAY_REMMD_ACCEPTANCE:
		case SUBMISSION_ACCEPTANCE: 
			if(!messageStatus.equals(PDomibusConnectorClientMessageStatus.CONFIRMED)) {
				if(!StringUtils.isEmpty(ebmsId)) {
					return PDomibusConnectorClientMessageStatus.RECEIVED;
				}else
					return PDomibusConnectorClientMessageStatus.ACCEPTED;
			}
		}
		return messageStatus;
	}

	public DomibusConnectorClientStorage getStorage() {
		return storage;
	}

	public void setStorage(DomibusConnectorClientStorage storage) {
		this.storage = storage;
	}

	public DomibusConnectorClientPersistenceService getPersistenceService() {
		return persistenceService;
	}

	public void setPersistenceService(DomibusConnectorClientPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

}
