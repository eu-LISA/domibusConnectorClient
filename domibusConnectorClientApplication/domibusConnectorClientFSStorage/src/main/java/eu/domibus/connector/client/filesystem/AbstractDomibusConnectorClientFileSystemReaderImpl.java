package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public abstract class AbstractDomibusConnectorClientFileSystemReaderImpl
//implements DomibusConnectorClientFileSystemReader 
{
	
	@Autowired
	private DomibusConnectorClientFSConfigurationProperties properties;

	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AbstractDomibusConnectorClientFileSystemReaderImpl.class);

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#readUnsentMessages(java.io.File)
	 */
//	@Override


	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#readMessagesFromDirWithPostfix(java.io.File, java.lang.String)
	 */
//	@Override
	public List<File> readMessagesFromDirWithPostfix(File messagesDir, String endsWith) {
		List<File> messages = new ArrayList<File>();

		if (messagesDir.listFiles().length > 0) {
			for (File sub : messagesDir.listFiles()) {
				if (sub.isDirectory()
						&& sub.getName().endsWith(endsWith)) {
					messages.add(sub);
				}
			}
		}
		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#readAllMessagesFromDir(java.io.File)
	 */
//	@Override
	public List<File> readAllMessagesFromDir(File messagesDir) {
		List<File> messages = new ArrayList<File>();

		if (messagesDir.listFiles().length > 0) {
			for (File sub : messagesDir.listFiles()) {
				if (sub.isDirectory() && sub.listFiles()!=null && sub.listFiles().length > 0) {
					messages.add(sub);
				}
			}
		}
		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#readMessageFromFolder(java.io.File)
	 */
//	@Override
	public DomibusConnectorMessageType readMessageFromFolder(File messageFolder) throws DomibusConnectorClientFileSystemException {
		


		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			String messageFolderPath = messageFolder.getAbsolutePath();
			LOGGER.info("Start reading message from folder {}", messageFolderPath);

			DomibusConnectorMessageType message = new DomibusConnectorMessageType();

			try {
				message = processMessageFolderFiles(messageFolder);
			} catch (Exception e) {
				LOGGER.error("#readMessageFromFolder: an error occured!", e);
				throw new DomibusConnectorClientFileSystemException("Could not process message folder "+messageFolder.getAbsolutePath());
			}

			return message;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#loadFileContentFromMessageFolder(java.io.File, java.lang.String)
	 */
//	@Override
	public byte[] loadFileContentFromMessageFolder(File messageFolder, String fileName) {
		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			
			String messageFolderPath = messageFolder.getAbsolutePath();
			LOGGER.debug("Start reading file {} from folder {}", fileName, messageFolderPath);
			
			for (File sub : messageFolder.listFiles()) {
				if(sub.getName().equals(fileName)) {
					LOGGER.debug("Found file with name {} and length {}", sub.getName(), sub.length());
					byte[] content = null;
					try {
						content = fileToByteArray(sub);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return content;
				}
				
				
			}
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#checkStorageStatusOfMessage(java.lang.String)
	 */
//	@Override
	public DomibusConnectorClientStorageStatus checkStorageStatusOfMessage(String storageLocation) {
		File storageFile = new File(storageLocation);

		//check if the storageLocation is a directory
		if(storageFile!=null && storageFile.exists() && storageFile.isDirectory()) {
			return DomibusConnectorClientStorageStatus.STORED;
		}
		

		//Non of the above, so the storageLocation does not exist anymore
		return DomibusConnectorClientStorageStatus.DELETED;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader#getFileListFromMessageFolder(java.io.File)
	 */
//	@Override
	public abstract Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(File messageFolder);

	protected abstract DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder) throws DomibusConnectorClientFileSystemException;

	protected boolean isFile(String filename, String messageProperty) {
		if(messageProperty!=null) {
			if(messageProperty.equals(filename))
				return true;
		}
		return false;
	}

	protected boolean isConfirmation(String filename) {
		if(filename!=null) {
			if(filename.indexOf(".xml")>0) {
				String name = filename.substring(0, filename.indexOf(".xml"));
				List<String> CONFIRMATION_NAMES = Arrays.asList(DomibusConnectorConfirmationType.values()).stream()
						.map(e -> e.name())
						.collect(Collectors.toList());
				if (CONFIRMATION_NAMES.contains(name)) {
					return true;
				}
			}
		}
		return false;
	}

	protected byte[] fileToByteArray(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}

}
