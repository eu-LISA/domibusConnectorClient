package eu.domibus.connector.client.filesystem.isupport.writer;

import java.io.File;
import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.AbstractDomibusConnectorClientFileSystemWriterImpl;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.filesystem.isupport.ISupportFSMessageProperties;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@Validated
@Valid
@Profile("iSupport")
public class ISupportFSWriterImpl extends AbstractDomibusConnectorClientFileSystemWriterImpl implements DomibusConnectorClientFileSystemWriter {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ISupportFSWriterImpl.class);
	
	@Autowired
	private DomibusConnectorClientFSConfigurationProperties properties;
	
	@Autowired
	private ISupportFSMessageProperties messageProperties;
	
	@Override
	protected void putFileToMessageProperties(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType) {
		// nothing to do here since iSupport does not use message properties

	}

	@Override
	protected void removeFileFromMessageProperties(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType) {
		// nothing to do here since iSupport does not use message properties

	}

	@Override
	protected void setMessageSentInMessageProperties(File messageFolder) {
		// nothing to do here since iSupport does not use message properties

	}

	@Override
	public String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir)
			throws DomibusConnectorClientFileSystemException {
		File iSupportIncomingMessagesDir = new File(messagesDir, messageProperties.getiSupportIncomingDir());
		
		if(!iSupportIncomingMessagesDir.exists()) {
			iSupportIncomingMessagesDir.mkdir();
			if(!iSupportIncomingMessagesDir.exists()) {
				LOGGER.error("Problem with directories.");
	            return null;
			}
			
		}
		
		File messageFolder = createMessageFolder(message, iSupportIncomingMessagesDir, true);
		

		LOGGER.debug("Write new message into folder {}", messageFolder.getAbsolutePath());
		
		DomibusConnectorMessageContentType messageContent = message.getMessageContent();
		if (messageContent != null) {
			if (messageContent.getDocument() != null) {
				String fileName = null;
				if (StringUtils.hasText(messageContent.getDocument().getDocumentName())) {
					fileName = messageContent.getDocument().getDocumentName();
				} else {
					fileName = properties.getDefaultPdfFileName();

				}
//				writeBusinessDocumentToFS(messageFolder, fileName, messageContent);
				DomibusConnectorDetachedSignatureType detachedSignature = messageContent.getDocument().getDetachedSignature();
				if (detachedSignature != null && detachedSignature.getDetachedSignature()!=null && detachedSignature.getDetachedSignature().length > 0
						&& detachedSignature.getMimeType() != null) {
					writeDetachedSignatureToFS(messageFolder, detachedSignature);
				}
			}
			if (messageContent.getXmlContent() != null){
				String fileName = messageProperties.getFileName();
				writeBusinessContentToFS(messageFolder, messageContent, fileName);
			}
		}
	
		if (message.getMessageAttachments() != null) {
			writeAttachmentsToFS(message, messageFolder);
		}

		if (message.getMessageConfirmations() != null) {
			writeConfirmationsToFS(message, messageFolder);
		}

		return messageFolder.getAbsolutePath();
	}
	
	@Override
	protected File createMessageFolder(DomibusConnectorMessageType message, File messagesDir, boolean createIfNonExistent) throws DomibusConnectorClientFileSystemException {
		String documentName=null;
		if(message.getMessageContent()!=null && message.getMessageContent().getDocument()!=null && message.getMessageContent().getDocument().getDocumentName()!=null) {
			documentName = message.getMessageContent().getDocument().getDocumentName();
		}
		
		if(StringUtils.hasText(documentName)) {
		
			LOGGER.debug("Creating message folder {} in iSupportIncomingMessagesDir {}", documentName, messagesDir.getAbsolutePath());
			
			File messageFolder = new File(messagesDir, documentName);
			if (createIfNonExistent && (!messageFolder.exists() || !messageFolder.isDirectory())) {
				LOGGER.debug("Message folder {} does not exist. Create folder!",
						messageFolder.getAbsolutePath());
				if (!messageFolder.mkdir()) {
					throw new DomibusConnectorClientFileSystemException(
							"Message folder cannot be created!");
				}
			}
		
			return messageFolder;
		}else {
			return super.createMessageFolder(message, messagesDir, createIfNonExistent);
		}
		
	}


}
