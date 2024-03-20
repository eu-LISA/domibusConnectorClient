package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.filesystem.standard.DefaultMessageProperties;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class DomibusConnectorClientFileSystemUtil {
	
	static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFileSystemUtil.class);
	
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HHmmss");
	
	public static File renameMessageFolder(File messageFolder, String folderPath, String newFolderPathExtension ) throws DomibusConnectorClientFileSystemException {
		File newMessageFolder = new File(folderPath + newFolderPathExtension);

		LOGGER.debug("Try to rename message folder {} to {}", messageFolder.getAbsolutePath(),
				newMessageFolder.getAbsolutePath());
		try {
			FileUtils.moveDirectory(messageFolder, newMessageFolder);
		} catch (IOException e1) {
			String error = "Could not rename folder "
					+ messageFolder.getAbsolutePath() + " to " + newMessageFolder.getAbsolutePath();
			LOGGER.error(error, e1);
			throw new DomibusConnectorClientFileSystemException(error);
		}
		
		return newMessageFolder;
	}
	


	public static String convertDateToProperty(Date date){
		return sdf2.format(date);
	}
	
	 public static String getMessageFolderName(DomibusConnectorMessageType message, String messageId) throws DomibusConnectorClientFileSystemException {
//			String backendMessageId = message.getMessageDetails().getBackendMessageId();
//			if(StringUtils.isEmpty(backendMessageId)) {
//				throw new DomibusConnectorClientFileSystemException("No backendMessageId set in message!");
//			}
//			
		 
		 	if(messageId==null || messageId.isEmpty()) {
		 		throw new DomibusConnectorClientFileSystemException("Neither ebmsId nor backendMessageId set in message!");
		 	}
		 	
		 	
		 	
			String fromPartyId = message.getMessageDetails().getFromParty().getPartyId();
			if(StringUtils.isEmpty(fromPartyId)) {
				throw new DomibusConnectorClientFileSystemException("No fromPartyId set in message!");
			}
			
			String toPartyId = message.getMessageDetails().getToParty().getPartyId();
			if(StringUtils.isEmpty(toPartyId)) {
				throw new DomibusConnectorClientFileSystemException("No toPartyId set in message!");
			}

			return new StringBuilder()
						.append(fromPartyId)
						.append("-")
						.append(toPartyId)
						.append("-")
						.append(messageId.replaceAll("[\\\\/:*?\"<>|]", "_"))
						.toString();
		}



	
}
