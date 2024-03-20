package eu.domibus.connector.client.filesystem;

import java.io.File;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;

public interface DomibusConnectorClientFSStorage extends DomibusConnectorClientStorage {

	void setMessagesDir(File messagesDir);



}
