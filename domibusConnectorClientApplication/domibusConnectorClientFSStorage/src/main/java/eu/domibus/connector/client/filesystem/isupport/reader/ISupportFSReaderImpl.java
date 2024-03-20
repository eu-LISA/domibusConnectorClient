package eu.domibus.connector.client.filesystem.isupport.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemReaderImpl;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.filesystem.isupport.ISupportFSMessageProperties;
import eu.domibus.connector.client.filesystem.isupport.sbdh.SBDHJaxbConverter;
import eu.domibus.connector.client.filesystem.isupport.sbdh.model.StandardBusinessDocumentHeader;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;

@Component
@Validated
@Valid
@Profile("iSupport")
public class ISupportFSReaderImpl extends AbstractDomibusConnectorClientFileSystemReaderImpl implements DomibusConnectorClientFileSystemReader {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ISupportFSReaderImpl.class);

	@Autowired
	private ISupportFSMessageProperties messageProperties;
	
	@Autowired
	private SBDHJaxbConverter sbdhConverter;
	
	@Autowired
	private DomibusConnectorClientFSConfigurationProperties properties;
	
	@Override
	public List<File> readUnsentMessages(File messagesDir){
		File outgoingMessagesDir = new File(messagesDir, messageProperties.getiSupportOutgoingDir());
		
		if(!outgoingMessagesDir.exists()) {
			outgoingMessagesDir.mkdir();
			if(!outgoingMessagesDir.exists()) {
				LOGGER.error("Problem with directories.");
	            return null;
			}
			
		}
		
//		LOGGER.debug("#readUnsentMessages: Searching for folders with ending {}", );
		List<File> messagesUnsent = new ArrayList<File>();

		if (outgoingMessagesDir.listFiles().length > 0) {
			for (File sub : outgoingMessagesDir.listFiles()) {
				if (sub.isDirectory()){
					File processedFile = new File(sub, messageProperties.getProcessedFileName());
					if(!processedFile.exists())
						messagesUnsent.add(sub);
				}
			}
		}

		return messagesUnsent;
	}

	@Override
	public Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(File messageFolder) {
		Map<String, DomibusConnectorClientStorageFileType> files = new HashMap<>();
		if (messageFolder.canRead() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {

			for (File sub : messageFolder.listFiles()) {

				if (sub.getName().equals(messageProperties.getFileName())) {
					LOGGER.debug("Found content xml file with name {}", sub.getName());
					files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_CONTENT);
					continue;
				} else if (isConfirmation(sub.getName())) {
					LOGGER.debug("Found confirmation file {}", sub.getName());
					files.put(sub.getName(), DomibusConnectorClientStorageFileType.CONFIRMATION);
					continue;
				} else {
					LOGGER.debug("Found attachment file {}", sub.getName());
					files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_ATTACHMENT);
				}

			}

		}
		return files;
	}

	@Override
	protected DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder)
			throws DomibusConnectorClientFileSystemException {
		DomibusConnectorMessageType message = new DomibusConnectorMessageType();

		StandardBusinessDocumentHeader sbdh = null;
		try {
			sbdh = sbdhConverter.getSBDH(workMessageFolder, this.messageProperties.getFileName());
		} catch (JAXBException | IOException e1) {
			throw new RuntimeException();
		}
		
		DomibusConnectorMessageDetailsType msgDetails = convertSBDHToMessageDetails(sbdh, workMessageFolder.getName());
		message.setMessageDetails(msgDetails);

		int attachmentCount = 1;

		DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
		DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();

		for (File sub : workMessageFolder.listFiles()) {
			
				if (isFile(sub.getName(),this.messageProperties.getFileName())) {
					LOGGER.debug("Found content xml file with name {}", sub.getName());
					try {
						if(LOGGER.isDebugEnabled()) {
							byte[] xmlBytes = fileToByteArray(sub);
							LOGGER.debug("Business content XML after read from file: {}", new String(xmlBytes));
						}
						messageContent.setXmlContent(ConversionTools.convertFileToStreamSource(sub));
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception creating Source object out of file " + sub.getName());
					}
					LOGGER.debug("Set {} as document name", workMessageFolder.getName());
					document.setDocumentName(workMessageFolder.getName());
					document.setDocument(ConversionTools.convertFileToDataHandler(sub, "text/xml"));
					continue;
				} else if (isConfirmation(sub.getName())) {
					DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
					confirmation.setConfirmation(ConversionTools.convertFileToStreamSource(sub));
					if(sub.getName().indexOf(".xml")>0) {
						String name = sub.getName().substring(0, sub.getName().indexOf(".xml"));
						DomibusConnectorConfirmationType valueOf = DomibusConnectorConfirmationType.valueOf(name);
						if(valueOf!=null) {
							confirmation.setConfirmationType(valueOf);
						}
					}
					message.getMessageConfirmations().add(confirmation);
				} else if (!isFile(sub.getName(), this.messageProperties.getFileName())){
					LOGGER.debug("Processing attachment File {}", sub.getName());

					byte[] attachmentData = null;
					try {
						attachmentData = fileToByteArray(sub);
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception loading attachment into byte array from file " + sub.getName());
					}
					String attachmentId = properties.getAttachmentIdPrefix() + attachmentCount;

					if(!ArrayUtils.isEmpty(attachmentData)){
						DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();

						attachment.setAttachment(ConversionTools.convertFileToDataHandler(sub, "application/octet-stream"));
						attachment.setIdentifier(attachmentId);
						attachment.setName(sub.getName());
						attachmentCount++;
						attachment.setMimeType(new MimetypesFileTypeMap().getContentType(sub.getName()));

						LOGGER.debug("Add attachment {}", attachment.toString());
						message.getMessageAttachments().add(attachment);
					}
				}
			
		}

		if (document.getDocument() != null) {
			messageContent.setDocument(document);
		}

		if(messageContent.getXmlContent()!=null)
			message.setMessageContent(messageContent);

		File messageProcessedFile = new File(workMessageFolder,messageProperties.getProcessedFileName());
		try {
			messageProcessedFile.createNewFile();
		} catch (IOException e) {
			throw new DomibusConnectorClientFileSystemException("File to mark message as processed could not be created for message "+workMessageFolder.getPath());
		}
		
		

		return message;
	}
	

	
	private DomibusConnectorMessageDetailsType convertSBDHToMessageDetails(StandardBusinessDocumentHeader sbdh, String messageFolderName) {

		DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();

//		messageDetails.setEbmsMessageId(properties.getMessageProperties().getProperty(messageProperties.getEbmsMessageId()));

		messageDetails.setFinalRecipient(sbdh.getTransport().getReceiver().getContactInformation().getContact());
		messageDetails.setOriginalSender(sbdh.getTransport().getSender().getContactInformation().getContact());
		
		messageDetails.setBackendMessageId(messageFolderName);


		
		String fromPartyId = sbdh.getTransport().getSender().getIdentifier();
		String fromPartyIdType = messageProperties.getFromPartyIdType();
		String fromPartyRole = messageProperties.getFromPartyRole();


		DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
		fromParty.setPartyId(fromPartyId);
		fromParty.setPartyIdType(fromPartyIdType);
		fromParty.setRole(fromPartyRole);
		messageDetails.setFromParty(fromParty);



		String toPartyId = sbdh.getTransport().getReceiver().getIdentifier();
		String toPartyIdType = messageProperties.getToPartyIdType();
		String toPartyRole = messageProperties.getToPartyRole();
		
		
		DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
		toParty.setPartyId(toPartyId);
		toParty.setPartyIdType(toPartyIdType);
		toParty.setRole(toPartyRole);
		messageDetails.setToParty(toParty);

		String action = messageProperties.getAction();
		DomibusConnectorActionType domibusConnectorAction = new DomibusConnectorActionType();
		domibusConnectorAction.setAction(action);
		messageDetails.setAction(domibusConnectorAction);

		String service = messageProperties.getService();
		String serviceType = messageProperties.getServiceType();
		DomibusConnectorServiceType domibusConnectorService = new DomibusConnectorServiceType();
		domibusConnectorService.setService(service);
		domibusConnectorService.setServiceType(serviceType);
		
		messageDetails.setService(domibusConnectorService);

		String conversationId = sbdh.getTransport().getCaseId();
		if(StringUtils.hasText(conversationId)){
			messageDetails.setConversationId(conversationId);
		}

		return messageDetails;
	}

}
