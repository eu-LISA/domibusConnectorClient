package eu.domibus.connector.client.controller.rest.util;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusConnectorClientRestUtil {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientRestUtil.class);

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientStorage storage;

	@Autowired
	private DomibusConnectorClientMessageBuilder messageBuilder;

	
	
	public DomibusConnectorClientMessage mapMessageFromModel(PDomibusConnectorClientMessage message, boolean loadFileContents) {
		DomibusConnectorClientMessage msg = new DomibusConnectorClientMessage();
		BeanUtils.copyProperties(message, msg);
		msg.setStorageStatus(message.getStorageStatus().name());
		msg.setMessageStatus(message.getMessageStatus().name());

		if(filesReadable(message)) {
			Set<PDomibusConnectorClientConfirmation> confirmations = message.getConfirmations();
			confirmations.forEach(confirmation -> {
				DomibusConnectorClientConfirmation evidence = new DomibusConnectorClientConfirmation();
				BeanUtils.copyProperties(confirmation, evidence);
				//			evidence.setStorageStatus(confirmation.getStorageStatus().name());
				if(loadFileContents) {
					try {
						byte[] content = storage.loadFileContentFromStorageLocation(message.getStorageInfo(), confirmation.getConfirmationType()+".xml");
						evidence.setConfirmation(content);
					} catch (IllegalArgumentException | DomibusConnectorClientStorageException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				msg.getEvidences().add(evidence);
			});

			Map<String, DomibusConnectorClientStorageFileType> files = null;
			try {
				files = storage.listContentAtStorageLocation(message.getStorageInfo());
			} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
				DomibusConnectorClientStorageStatus checkStorageStatus = storage.checkStorageStatus(message.getStorageInfo());
				message.setStorageStatus(checkStorageStatus);
				persistenceService.mergeClientMessage(message);
				return msg;
				
			}
			files.entrySet().forEach(file -> {
				DomibusConnectorClientMessageFileType fileType = DomibusConnectorClientMessageFileType.valueOf(file.getValue().name());
				DomibusConnectorClientMessageFile file2 = new DomibusConnectorClientMessageFile(file.getKey(), fileType);
				file2.setStorageLocation(message.getStorageInfo());
				if(loadFileContents) {
				try {
					byte[] fileContent = storage.loadFileContentFromStorageLocation(message.getStorageInfo(), file.getKey());
					file2.setFileContent(fileContent);
				} catch (IllegalArgumentException | DomibusConnectorClientStorageException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}
				msg.getFiles().add(file2);
			});
		}

		return msg;
	}
	
	public PDomibusConnectorClientMessage mapMessageToModel(DomibusConnectorClientMessage msg, PDomibusConnectorClientMessage pMessage) {
		if(pMessage == null) {
			pMessage = new PDomibusConnectorClientMessage();
		}
		BeanUtils.copyProperties(msg, pMessage);

		return pMessage;
	}

	public DomibusConnectorMessageType mapMessageToTransition(DomibusConnectorClientMessage clientMessage){
		DomibusConnectorMessageType message = messageBuilder.createNewMessage(
				clientMessage.getBackendMessageId(), 
				clientMessage.getEbmsMessageId(), 
				clientMessage.getConversationId(), 
				clientMessage.getService(), clientMessage.getServiceType(),
				clientMessage.getAction(), 
				clientMessage.getFromPartyId(), clientMessage.getFromPartyType(), clientMessage.getFromPartyRole(), 
				clientMessage.getToPartyId(), clientMessage.getToPartyType(), clientMessage.getToPartyRole(), 
				clientMessage.getFinalRecipient(), clientMessage.getOriginalSender());

		if(clientMessage.getFiles()!=null && clientMessage.getFiles().getFiles()!=null && !clientMessage.getFiles().getFiles().isEmpty()) {
			clientMessage.getFiles().getFiles().forEach(file -> {
				byte[] fileContent = file.getFileContent();
				if(fileContent==null) {
					try {
						fileContent = storage.loadFileContentFromStorageLocation(clientMessage.getStorageInfo(), file.getFileName());
					} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
						LOGGER.error("Exception called storage.loadFileContentFromStorageLocation with storageLocation {} and fileName {}", clientMessage.getStorageInfo(), file.getFileName(), e);
						
					}
				}
				if(fileContent != null && fileContent.length > 0) {
					if(file.getFileType().name().equals(DomibusConnectorClientStorageFileType.BUSINESS_CONTENT.name())) {
						messageBuilder.addBusinessContentXMLAsBinary(message, fileContent);
					}else if(file.getFileType().name().equals(DomibusConnectorClientStorageFileType.BUSINESS_DOCUMENT.name())) {
						messageBuilder.addBusinessDocumentAsBinary(message, fileContent, file.getFileName());
					}else {
						messageBuilder.addBusinessAttachmentAsBinaryToMessage(message, file.getFileName(), fileContent, file.getFileName(), null, file.getFileName());
					}

				}
			});
		}
		
		if(clientMessage.getEvidences()!=null && !clientMessage.getEvidences().isEmpty()) {
			clientMessage.getEvidences().forEach(confirmation ->{
				DomibusConnectorMessageConfirmationType msgConf = new DomibusConnectorMessageConfirmationType();
				msgConf.setConfirmationType(DomibusConnectorConfirmationType.valueOf(confirmation.getConfirmationType()));
				message.getMessageConfirmations().add(msgConf);
			});
		}
		
		
		return message;
	}
	
	public DomibusConnectorClientMessageList mapMessagesFromModel(Iterable<PDomibusConnectorClientMessage> findAll, boolean loadFileContents)  {
		DomibusConnectorClientMessageList messages = new DomibusConnectorClientMessageList();

		findAll.forEach(message -> {
			DomibusConnectorClientMessage msg = mapMessageFromModel(message, loadFileContents);
			messages.getMessages().add(msg);
		});

		return messages;
	}

	private boolean filesReadable(PDomibusConnectorClientMessage message) {
		return message!=null && message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
	}

}
