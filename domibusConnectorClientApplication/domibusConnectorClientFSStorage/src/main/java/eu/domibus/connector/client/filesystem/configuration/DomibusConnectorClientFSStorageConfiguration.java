package eu.domibus.connector.client.filesystem.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorage;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorageImpl;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemWriter;
import eu.domibus.connector.client.filesystem.standard.reader.DefaultFSReaderImpl;
import eu.domibus.connector.client.filesystem.standard.writer.DefaultFSWriterImpl;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;

@Configuration
@ConditionalOnProperty(prefix=DomibusConnectorClientFSStorageConfiguration.PREFIX, name=DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue="true", matchIfMissing=true)
@PropertySource("classpath:/connector-client-fs.properties")
@EnableConfigurationProperties({DomibusConnectorClientFSConfigurationProperties.class, DirectoryConfigurationConfigurationProperties.class})
public class DomibusConnectorClientFSStorageConfiguration {
	
	public static final String PREFIX = "connector-client.storage.filesystem";
    public static final String ENABLED_PROPERTY_NAME = "enabled";
    

    
//    @NestedConfigurationProperty
//    @NotNull
//    private DomibusConnectorClientFSMessageProperties messageProperties;
//    
//    @NestedConfigurationProperty
//    @NotNull
//    private DomibusConnectorClientFSProperties properties;

	public DomibusConnectorClientFSStorageConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
    @Bean
	public DomibusConnectorClientStorage domibusConnectorClientFSStorage(DirectoryConfigurationConfigurationProperties messages) {
    	DomibusConnectorClientFSStorage fsStorage = new DomibusConnectorClientFSStorageImpl();
		
		fsStorage.setMessagesDir(messages.getPath().toFile());
		
		return fsStorage;
	}
    
    @Bean
    @ConditionalOnMissingBean({DomibusConnectorClientFileSystemReader.class})
    public DomibusConnectorClientFileSystemReader domibusConnectorClientFileSystemReader() {
    	
    	return new DefaultFSReaderImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean({DomibusConnectorClientFileSystemWriter.class})
    public DomibusConnectorClientFileSystemWriter domibusConnectorClientFileSystemWriter() {
    	return new DefaultFSWriterImpl();
    }

	
//	public DirectoryConfigurationConfigurationProperties getMessages() {
//		return messages;
//	}
//
//	public void setMessages(DirectoryConfigurationConfigurationProperties messages) {
//		this.messages = messages;
//	}

//	public DomibusConnectorClientFSMessageProperties getMessageProperties() {
//		return messageProperties;
//	}
//
//	public void setMessageProperties(DomibusConnectorClientFSMessageProperties messageProperties) {
//		this.messageProperties = messageProperties;
//	}
//
//	public DomibusConnectorClientFSProperties getProperties() {
//		return properties;
//	}
//
//	public void setProperties(DomibusConnectorClientFSProperties properties) {
//		this.properties = properties;
//	}

}
