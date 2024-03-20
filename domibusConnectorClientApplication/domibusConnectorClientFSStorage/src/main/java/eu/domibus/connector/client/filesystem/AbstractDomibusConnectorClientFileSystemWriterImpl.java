package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;

//@Component
//@ConfigurationProperties(prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX)
//@PropertySource("classpath:/connector-client-fs-storage-default.properties")
//@Validated
//@Valid
public abstract class AbstractDomibusConnectorClientFileSystemWriterImpl 
//implements DomibusConnectorClientFileSystemWriter 
{

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AbstractDomibusConnectorClientFileSystemWriterImpl.class);

	
	@Autowired
	private DomibusConnectorClientFSConfigurationProperties properties;


	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#writeConfirmationToFileSystem(eu.domibus.connector.domain.transition.DomibusConnectorMessageType, java.io.File)
	 */
//	@Override
	public void writeConfirmationToFileSystem(DomibusConnectorMessageType confirmationMessage, File messageFolder ) throws DomibusConnectorClientFileSystemException {
		DomibusConnectorMessageConfirmationType confirmation = confirmationMessage.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();
		
		String path = messageFolder.getAbsolutePath() + File.separator + type + properties.getXmlFileExtension();;

		LOGGER.debug("Create confirmation file {}", path);
		File evidenceXml = new File(path);
		try {
			byte[] xmlBytes = ConversionTools.convertXmlSourceToByteArray(confirmation.getConfirmation());
			byteArrayToFile(xmlBytes, evidenceXml);
		} catch (IOException e) {
			throw new DomibusConnectorClientFileSystemException("Could not create file "
					+ evidenceXml.getAbsolutePath(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#writeMessageFileToFileSystem(java.io.File, java.lang.String, eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType, byte[])
	 */
//	@Override
	public void writeMessageFileToFileSystem(File messageFolder, String fileName, DomibusConnectorClientStorageFileType fileType, byte[] fileContent) throws DomibusConnectorClientFileSystemException {
		
		
		putFileToMessageProperties(messageFolder, fileName, fileType);
		
		try {
			createFile(messageFolder, fileName, fileContent);
		} catch (IOException e1) {
			throw new DomibusConnectorClientFileSystemException("Could not create file "+fileName+" at message folder "
					+ messageFolder.getAbsolutePath(), e1);
		}
		
	}

	protected abstract void putFileToMessageProperties(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType);
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#deleteMessageFileFromFileSystem(java.io.File, java.lang.String, eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType)
	 */
//	@Override
	public void deleteMessageFileFromFileSystem(File messageFolder, String fileName, DomibusConnectorClientStorageFileType fileType) throws DomibusConnectorClientFileSystemException {
		
		removeFileFromMessageProperties(messageFolder, fileName, fileType);
		
		deleteFile(messageFolder, fileName);
		
	}

	protected abstract void removeFileFromMessageProperties(File messageFolder, String fileName,
			DomibusConnectorClientStorageFileType fileType);
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#updateMessageAtStorageToSent(java.io.File)
	 */
//	@Override
	public String updateMessageAtStorageToSent(File messageFolder) throws DomibusConnectorClientFileSystemException {
		
		setMessageSentInMessageProperties(messageFolder);
		
		if(messageFolder.getAbsolutePath().endsWith(properties.getMessageReadyPostfix())) {
			String newFolderName = messageFolder.getAbsolutePath().substring(0, messageFolder.getAbsolutePath().length() - properties.getMessageReadyPostfix().length());
			File newMessageFolder = new File(newFolderName);
			messageFolder.renameTo(newMessageFolder);
			return newFolderName;
		}
		return messageFolder.getAbsolutePath();
	}

	protected abstract void setMessageSentInMessageProperties(File messageFolder);

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#deleteFromStorage(java.io.File)
	 */
//	@Override
	public void deleteFromStorage(File messageFolder) throws DomibusConnectorClientFileSystemException {
		try {
			deleteDirectory(messageFolder);
		} catch (Exception e) {
			throw new DomibusConnectorClientFileSystemException("Message folder and/or its contents could not be deleted! ", e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter#writeMessageToFileSystem(eu.domibus.connector.domain.transition.DomibusConnectorMessageType, java.io.File)
	 */
//	@Override
	public abstract String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir) throws DomibusConnectorClientFileSystemException;
	
	protected void writeBusinessContentToFS(File messageFolder, DomibusConnectorMessageContentType messageContent,
			String fileName) {
		try {
			byte[] content = ConversionTools.convertXmlSourceToByteArray(messageContent.getXmlContent());
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Business content XML before written to file: {}", new String(content));
			}
			createFile(messageFolder, fileName, content);
		}catch(IOException | TransformerFactoryConfigurationError e) {
			LOGGER.error("Could not process business content file {} at messageFolder {}", fileName, messageFolder.getAbsolutePath(), e);
		}
	}

	protected void writeBusinessDocumentToFS(File messageFolder, String fileName,
			DomibusConnectorMessageContentType messageContent) {
		
		byte[] document;
		try {
			document = ConversionTools.convertDataHandlerToByteArray(messageContent.getDocument().getDocument());
			createFile(messageFolder, fileName, document);
		} catch (IOException e) {
			LOGGER.error("Could not process business document file {} at messageFolder {}", fileName, messageFolder.getAbsolutePath(), e);
//					throw new DomibusConnectorClientFileSystemException("Could not process document! ", e);
		}
	}

	protected String writeDetachedSignatureToFS(File messageFolder,
			DomibusConnectorDetachedSignatureType detachedSignature) {
		String fileName2 = null;
		if (StringUtils.hasText(detachedSignature.getDetachedSignatureName())
				&& !detachedSignature.getDetachedSignatureName().equals(
						properties.getDefaultDetachedSignatureFileName())) {
			fileName2 = detachedSignature.getDetachedSignatureName();
		} else {
			fileName2 = properties.getDefaultDetachedSignatureFileName();
			if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.XML))
				fileName2 += properties.getXmlFileExtension();
			else if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.PKCS_7))
				fileName2 += properties.getPkcs7FileExtension();

		}
		try {
			createFile(messageFolder, fileName2, detachedSignature.getDetachedSignature());
		} catch (IOException e) {
			LOGGER.error("Could not process detached signature file {} at messageFolder {}", fileName2, messageFolder.getAbsolutePath(), e);
//						throw new DomibusConnectorClientFileSystemException("Could not process document! ", e);
		}
		return fileName2;
	}

	protected void writeAttachmentsToFS(DomibusConnectorMessageType message, File messageFolder) {
		for (DomibusConnectorMessageAttachmentType attachment : message.getMessageAttachments()) {
			try {
				byte[] attachmentBytes = ConversionTools.convertDataHandlerToByteArray(attachment.getAttachment());
				createFile(messageFolder, attachment.getName(), attachmentBytes);
			} catch (IOException e) {
				LOGGER.error("Could not process business attachment file {} at messageFolder {}", attachment.getName(), messageFolder.getAbsolutePath(), e);
			}
		}
	}

	protected void writeConfirmationsToFS(DomibusConnectorMessageType message, File messageFolder) {
		for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
			String fileName = confirmation.getConfirmationType().name()
					+ properties.getXmlFileExtension();
			try {
				byte[] confirmationBytes = ConversionTools.convertXmlSourceToByteArray(confirmation.getConfirmation());
				createFile(messageFolder, fileName, confirmationBytes);
			} catch (IOException e) {
				LOGGER.error("Could not process confirmation file {} at messageFolder {}",fileName, messageFolder.getAbsolutePath(), e);
			}
		}
	}
	
	
	
	private void deleteDirectory(File directory) throws Exception {
		List<File> filesToBeDeleted = new ArrayList<File>();
		
		for (File sub : directory.listFiles()) {
			filesToBeDeleted.add(sub);
		}
		
		if(!filesToBeDeleted.isEmpty()) {
			for(File sub: filesToBeDeleted) {
				if(sub.isDirectory()) 
					deleteDirectory(sub);
				else
					sub.delete();
			}
		}
		
		directory.delete();
	}

	

	private void createFile(File messageFolder, String fileName, byte[] content)
			throws IOException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		File file = new File(filePath);
		if(!file.exists()) {
			LOGGER.debug("Create file {}", filePath);
			byteArrayToFile(content, file);
		}
	}
	
	private void deleteFile(File messageFolder, String fileName) throws DomibusConnectorClientFileSystemException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		File file = new File(filePath);
		if(file.exists()) {
			LOGGER.debug("delete file {}", filePath);
			if(!file.delete()){
				throw new DomibusConnectorClientFileSystemException("File "+file.getAbsolutePath()+" could not be deleted!");
			}
		}
	}

	private void byteArrayToFile(byte[] data, File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
	}


	protected File createMessageFolder(DomibusConnectorMessageType message, File messagesDir, boolean createIfNonExistent) throws DomibusConnectorClientFileSystemException {

		String messageId = message.getMessageDetails().getEbmsMessageId()!=null && !message.getMessageDetails().getEbmsMessageId().isEmpty()?message.getMessageDetails().getEbmsMessageId():message.getMessageDetails().getBackendMessageId();
		String pathname = new StringBuilder()
				.append(messagesDir.getAbsolutePath())
				.append(File.separator)
				.append(DomibusConnectorClientFileSystemUtil.getMessageFolderName(message, messageId ))
				.toString();
		File messageFolder = new File(pathname);
		if (createIfNonExistent && (!messageFolder.exists() || !messageFolder.isDirectory())) {
			LOGGER.debug("Message folder {} does not exist. Create folder!",
					messageFolder.getAbsolutePath());
			if (!messageFolder.mkdir()) {
				throw new DomibusConnectorClientFileSystemException(
						"Message folder cannot be created!");
			}
		}

		return messageFolder;
	}





}
