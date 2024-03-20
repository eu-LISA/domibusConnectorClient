package eu.domibus.connector.client.filesystem.standard.reader;

import eu.domibus.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemReaderImpl;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.filesystem.standard.DefaultMessageProperties;
import eu.domibus.connector.client.filesystem.standard.DomibusConnectorClientFSMessageProperties;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
//@Validated
//@Valid
//@ConditionalOnMissingBean({DomibusConnectorClientFileSystemReader.class})
public class DefaultFSReaderImpl extends AbstractDomibusConnectorClientFileSystemReaderImpl implements DomibusConnectorClientFileSystemReader {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultFSReaderImpl.class);

	@Autowired
	private DomibusConnectorClientFSMessageProperties messageProperties;

	@Autowired
	private DomibusConnectorClientFSConfigurationProperties properties;

	
	@Override
	public List<File> readUnsentMessages(File outgoingMessagesDir){
		String messageReadyPostfix = properties.getMessageReadyPostfix();
		if (messageReadyPostfix == null) {
			messageReadyPostfix = "";
		}
		LOGGER.debug("#readUnsentMessages: Searching for folders with ending {}", messageReadyPostfix);
		List<File> messagesUnsent = new ArrayList<File>();

		if (outgoingMessagesDir.listFiles().length > 0) {
			for (File sub : outgoingMessagesDir.listFiles()) {
				if (sub.isDirectory()
						&& sub.getName().endsWith(messageReadyPostfix)) {
					messagesUnsent.add(sub);
				}
			}
		}

		return messagesUnsent;
	}

	public Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(File messageFolder){
		Map<String, DomibusConnectorClientStorageFileType> files = new HashMap<String, DomibusConnectorClientStorageFileType>();
		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {

			DefaultMessageProperties messageDetails = new DefaultMessageProperties(messageFolder, this.messageProperties.getFileName());

			for (File sub : messageFolder.listFiles()) {

				if (sub.getName().equals(messageProperties.getFileName())) {
					continue;
				} else {

					if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getContentXmlFileName()))) {
						LOGGER.debug("Found content xml file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_CONTENT);
						continue;
					} else if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getContentPdfFileName()))) {
						LOGGER.debug("Found content pdf file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_DOCUMENT);
						continue;
					} else if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getDetachedSignatureFileName()))) {
						LOGGER.debug("Found detached signature file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.DETACHED_SIGNATURE);
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

		}
		return files;
	}

	protected DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder)
			throws DomibusConnectorClientFileSystemException {

		DomibusConnectorMessageType message = new DomibusConnectorMessageType();

		DefaultMessageProperties messageDetails = new DefaultMessageProperties(workMessageFolder, this.messageProperties.getFileName());

		DomibusConnectorMessageDetailsType msgDetails = convertMessagePropertiesToMessageDetails(messageDetails);
		message.setMessageDetails(msgDetails);

		int attachmentCount = 1;

		DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
		DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();

		for (File sub : workMessageFolder.listFiles()) {
			if (sub.getName().equals(messageDetails.getMessageProperties().getProperty(messageProperties.getFileName()))) {
				continue;
			} else {

				if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getContentXmlFileName()))) {
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
					continue;
				} else if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getContentPdfFileName()))) {
					LOGGER.debug("Found content pdf file with name {}", sub.getName());
					document.setDocument(ConversionTools.convertFileToDataHandler(sub, "application/octet-stream"));
					document.setDocumentName(sub.getName());
					continue;
				} else if (isFile(sub.getName(),messageDetails.getMessageProperties().getProperty(messageProperties.getDetachedSignatureFileName()))) {
					LOGGER.debug("Found detached signature file with name {}", sub.getName());
					try {
						DomibusConnectorDetachedSignatureType det = new DomibusConnectorDetachedSignatureType();
						det.setDetachedSignature(fileToByteArray(sub));
						det.setDetachedSignatureName(sub.getName());
						if (sub.getName().endsWith(properties.getXmlFileExtension())) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.XML);
						} else if (sub.getName()
								.endsWith(properties.getPkcs7FileExtension())) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);
						} else {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.BINARY);
						}
						document.setDetachedSignature(det );
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception loading detached signature into byte array from file " + sub.getName());
					}
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
		}

		if (document.getDocument() != null) {
			messageContent.setDocument(document);
		}

		if(messageContent.getXmlContent()!=null)
			message.setMessageContent(messageContent);


		return message;
	}

	private DomibusConnectorMessageDetailsType convertMessagePropertiesToMessageDetails(DefaultMessageProperties properties) {

		DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();

		messageDetails.setEbmsMessageId(properties.getMessageProperties().getProperty(messageProperties.getEbmsMessageId()));

		messageDetails.setFinalRecipient(properties.getMessageProperties().getProperty(messageProperties.getFinalRecipient()));
		messageDetails.setOriginalSender(properties.getMessageProperties().getProperty(messageProperties.getOriginalSender()));
		if(StringUtils.hasText(properties.getMessageProperties().getProperty(messageProperties.getBackendMessageId())))
			messageDetails.setBackendMessageId(properties.getMessageProperties().getProperty(messageProperties.getBackendMessageId()));


		String fromPartyId = properties.getMessageProperties().getProperty(messageProperties.getFromPartyId());
		String fromPartyRole = properties.getMessageProperties().getProperty(messageProperties.getFromPartyRole());


		DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
		fromParty.setPartyId(fromPartyId);
		fromParty.setRole(fromPartyRole);
		messageDetails.setFromParty(fromParty);



		String toPartyId = properties.getMessageProperties().getProperty(messageProperties.getToPartyId());
		String toPartyRole = properties.getMessageProperties().getProperty(messageProperties.getToPartyRole());
		DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
		toParty.setPartyId(toPartyId);
		toParty.setRole(toPartyRole);
		messageDetails.setToParty(toParty);

		String action = properties.getMessageProperties().getProperty(messageProperties.getAction());
		DomibusConnectorActionType domibusConnectorAction = new DomibusConnectorActionType();
		domibusConnectorAction.setAction(action);
		messageDetails.setAction(domibusConnectorAction);

		String service = properties.getMessageProperties().getProperty(messageProperties.getService());
		DomibusConnectorServiceType domibusConnectorService = new DomibusConnectorServiceType();
		domibusConnectorService.setService(service);
		domibusConnectorService.setServiceType(properties.getMessageProperties().getProperty(messageProperties.getServiceType()));
		messageDetails.setService(domibusConnectorService);

		String conversationId = properties.getMessageProperties().getProperty(messageProperties.getConversationId());
		if(StringUtils.hasText(conversationId)){
			messageDetails.setConversationId(conversationId);
		}

		return messageDetails;
	}

}
