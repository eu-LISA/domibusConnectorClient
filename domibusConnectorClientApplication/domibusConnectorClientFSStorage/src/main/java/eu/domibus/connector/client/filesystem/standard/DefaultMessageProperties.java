package eu.domibus.connector.client.filesystem.standard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemUtil;

public class DefaultMessageProperties {

	static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DefaultMessageProperties.class);

	private final Properties messageProperties = new Properties();

    public Properties getMessageProperties() {
		return messageProperties;
	}
    
    public DefaultMessageProperties() {
    	
    }

    public DefaultMessageProperties(File message, String messagePropertiesFileName) {
    	String pathname = message.getAbsolutePath() + File.separator + messagePropertiesFileName;
		LOGGER.debug("Loading message properties from file {}", pathname);
		File messagePropertiesFile = new File(pathname);
		if (!messagePropertiesFile.exists()) {
			LOGGER.error("Message properties file '" + messagePropertiesFile.getAbsolutePath()
			+ "' does not exist. Message cannot be processed!");
		}
		loadPropertiesFromFile(messagePropertiesFile);

	}

	public void loadPropertiesFromFile(File messagePropertiesFile) {
        // properties = new Properties();
        
        try (FileInputStream fileInputStream = new FileInputStream(messagePropertiesFile)) {            
            messageProperties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

    }
	
	public void storeMessagePropertiesToFile(File messagePropertiesFile) {
        if (!messagePropertiesFile.exists()) {
            try {
                messagePropertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

           
        try (FileOutputStream fos = new FileOutputStream(messagePropertiesFile) ) {                    
            messageProperties.store(fos, null);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

    }
}
