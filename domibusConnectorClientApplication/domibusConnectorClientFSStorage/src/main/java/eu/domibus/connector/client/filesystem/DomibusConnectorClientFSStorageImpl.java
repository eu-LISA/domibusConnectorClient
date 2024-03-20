package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class DomibusConnectorClientFSStorageImpl implements DomibusConnectorClientFSStorage {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFSStorageImpl.class);
	
	@Autowired
	DomibusConnectorClientFileSystemReader fileSystemReader;
	
	@Autowired
	DomibusConnectorClientFileSystemWriter fileSystemWriter;
	
	File messagesDir;
	
	@Override
	public String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException {
		
		LOGGER.debug("#storeMessage: storing message [{}]...", message);
		
		String messageLocation = null;
		try {
			messageLocation = fileSystemWriter.writeMessageToFileSystem(message, messagesDir);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing message [{}] from connector... ", message, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		LOGGER.debug("#storeMessage: message [{}] successfully stored.", message);
		
		return messageLocation;
	}

	@Override
	public void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();

		LOGGER.debug("#storeMessage: storing confirmation of type [{}] to message [{}]...", type, message.getMessageDetails().getRefToMessageId());
		
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		try {
			fileSystemWriter.writeConfirmationToFileSystem(message, messageFolder);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing confirmation [{}] to message from connector... ",type, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		
		LOGGER.debug("#storeMessage: confirmation [{}] to message [{}] successfully stored.", type, message.getMessageDetails().getRefToMessageId());
	}


	@Override
	public Map<String, DomibusConnectorMessageType> checkStorageForNewMessages()
			{
		LOGGER.debug("#checkStorageForNewMessages: Start searching dir {} for unsent messages.", messagesDir.getAbsolutePath());
		List<File> messagesUnsent = fileSystemReader.readUnsentMessages(messagesDir);

		if (!messagesUnsent.isEmpty()) {
			Map<String, DomibusConnectorMessageType> newMessages = new HashMap<String,DomibusConnectorMessageType>();
			LOGGER.info("#checkStorageForNewMessages: Found {} new outgoing messages to process!", messagesUnsent.size());
			for (File messageFolder : messagesUnsent) {
				LOGGER.debug("#checkStorageForNewMessages: Processing new message folder {}", messageFolder.getAbsolutePath());
				if (messageFolder.listFiles().length > 0) {
					
					DomibusConnectorMessageType message = null;
					try {
						message = fileSystemReader.readMessageFromFolder(messageFolder);
					} catch (DomibusConnectorClientFileSystemException e) {
						LOGGER.error(""+e);
						continue;
					}
					
					if(message!=null) {
						newMessages.put(messageFolder.getAbsolutePath(), message);
					}
				}
			}
			return newMessages;
		} else {
			LOGGER.debug("#checkStorageForNewMessages: No new messages found!");
			return null;
		}
	}
	
	@Override
	public DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation) {
		LOGGER.debug("#checkStorageStatus: check storage status at storage location "+storageLocation);
		return fileSystemReader.checkStorageStatusOfMessage(storageLocation);
	}
	
	@Override
	public Map<String, DomibusConnectorMessageType> getAllStoredMessages() {
		LOGGER.trace("#getAllStoredMessages: enter");
		List<File> readAllMessagesFromDir = fileSystemReader.readAllMessagesFromDir(messagesDir);
		if(!readAllMessagesFromDir.isEmpty()) {
			LOGGER.debug("#getAllStoredMessages: Found {} stored messages", readAllMessagesFromDir.size());
			Map<String, DomibusConnectorMessageType> allMessages = new HashMap<String, DomibusConnectorMessageType>();
			readAllMessagesFromDir.forEach(fileFolder -> {
				DomibusConnectorMessageType message = null;
				try {
					message = fileSystemReader.readMessageFromFolder(fileFolder);
					allMessages.put(fileFolder.getAbsolutePath(), message);
				} catch (DomibusConnectorClientFileSystemException e) {
					LOGGER.error("Exception read message from folder {}", fileFolder.getAbsolutePath(), e);
				}
			});
			
			return allMessages;
		}
		return null;
	}
	
	
	@Override
	public byte[] loadFileContentFromStorageLocation(String storageLocation, String fileName) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#loadFileContentFromStorageLocation: loading content of file {} from storageLocation {}", fileName, storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		return fileSystemReader.loadFileContentFromMessageFolder(messageFolder, fileName);
	}

	@Override
	public Map<String, DomibusConnectorClientStorageFileType> listContentAtStorageLocation(String storageLocation) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#listContentAtStorageLocation: list content at storageLocation {}", storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		return fileSystemReader.getFileListFromMessageFolder(messageFolder);
	}
	
	@Override
	public void storeFileIntoStorage(String storageLocation, String fileName,
			DomibusConnectorClientStorageFileType fileType, byte[] content) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#storeFileIntoStorage: store file {} of type {} into storageLocation {}", fileName, fileType.name(), storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		try {
			fileSystemWriter.writeMessageFileToFileSystem(messageFolder, fileName, fileType, content);
		} catch (DomibusConnectorClientFileSystemException e) {
			throw new DomibusConnectorClientStorageException("Exception storing file!", e);
		}
	}
	
	@Override
	public void deleteFileFromStorage(String storageLocation, String fileName, DomibusConnectorClientStorageFileType fileType) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#deleteFileFromStorage: delete file {} of type {} from storageLocation{}", fileName, fileType.name(), storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		try {
			fileSystemWriter.deleteMessageFileFromFileSystem(messageFolder, fileName, fileType);
		} catch (DomibusConnectorClientFileSystemException e) {
			throw new DomibusConnectorClientStorageException("Exception delete file!", e);
		}
		
	}
	
	@Override
	public DomibusConnectorMessageType getStoredMessage(String storageLocation)
			throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#getStoredMessage: load stored message from storageLocation {}", storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		DomibusConnectorMessageType message = null;
		try {
			message = fileSystemReader.readMessageFromFolder(messageFolder);
		} catch (DomibusConnectorClientFileSystemException e) {
			throw new DomibusConnectorClientFileSystemException("Exception read message from folder "+ messageFolder.getAbsolutePath(), e);
		}
		
		return message;
	}
	
	@Override
	public String updateStoredMessageToSent(String storageLocation) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#updateStoredMessageToSent: update at storageLocation {}", storageLocation);
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		String newStorageLocation = fileSystemWriter.updateMessageAtStorageToSent(messageFolder);
		
		LOGGER.debug("#updateStoredMessageToSent: new storageLocation after update is {}", newStorageLocation);
		return newStorageLocation;
	}



	@Override
	public void deleteMessageFromStorage(String storageLocation) throws DomibusConnectorClientStorageException, IllegalArgumentException {
		LOGGER.debug("#deleteMessageFromStorage: called with storageLocation {}", storageLocation);
		
		File messageFolder = checkStorageLocationAndMessageFolder(storageLocation);
		
		this.fileSystemWriter.deleteFromStorage(messageFolder);
		
		LOGGER.debug("#deleteMessageFromStorage: successfully deleted message at storageLocation {}", storageLocation);
	}
	
	private File checkStorageLocationAndMessageFolder(String storageLocation) throws DomibusConnectorClientFileSystemException, IllegalArgumentException {
		if(storageLocation == null || storageLocation.isEmpty()) {
			throw new IllegalArgumentException("Storage location is null or empty! ");
		}
		
		File messageFolder = new File(storageLocation);
		if(!messageFolder.exists() || !messageFolder.isDirectory()) {
			throw new DomibusConnectorClientFileSystemException("Storage location is not valid! "+storageLocation);
		}
		
		return messageFolder;
	}

	
	public File getMessagesDir() {
		return messagesDir;
	}
	
	@Override
	public void setMessagesDir(File messagesDir) {
		this.messagesDir = messagesDir;
	}






}
