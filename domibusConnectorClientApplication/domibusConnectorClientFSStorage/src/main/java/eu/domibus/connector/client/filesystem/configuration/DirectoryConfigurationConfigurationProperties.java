package eu.domibus.connector.client.filesystem.configuration;

import java.nio.file.Path;

import eu.domibus.connector.lib.spring.configuration.validation.CheckFolderWriteable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@ConfigurationProperties(prefix = DirectoryConfigurationConfigurationProperties.PREFIX)
@Validated
@Valid
public class DirectoryConfigurationConfigurationProperties {

	public static final String PREFIX = "connector-client.storage.filesystem.messages";
	@CheckFolderWriteable
	private Path path;
	private boolean createIfNonExistent;
	
	public DirectoryConfigurationConfigurationProperties() {
		// TODO Auto-generated constructor stub
	}


	public boolean isCreateIfNonExistent() {
		return createIfNonExistent;
	}

	public void setCreateIfNonExistent(boolean createIfNonExistent) {
		this.createIfNonExistent = createIfNonExistent;
	}


	public Path getPath() {
		return path;
	}


	public void setPath(Path path) {
		this.path = path;
	}


	@Override
	public String toString() {
		return "DirectoryConfigurationProperties [path=" + path + ", createIfNonExistent=" + createIfNonExistent + "]";
	}

}
