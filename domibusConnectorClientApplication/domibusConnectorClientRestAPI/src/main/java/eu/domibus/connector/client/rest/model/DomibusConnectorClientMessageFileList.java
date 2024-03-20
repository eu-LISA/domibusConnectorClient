package eu.domibus.connector.client.rest.model;

import java.util.ArrayList;
import java.util.List;

public class DomibusConnectorClientMessageFileList {

	private List<DomibusConnectorClientMessageFile> files;

	
	public DomibusConnectorClientMessageFileList() {
		setFiles(new ArrayList<DomibusConnectorClientMessageFile>());
	}
	
	public void add(DomibusConnectorClientMessageFile file) {
		getFiles().add(file);
	}


	public List<DomibusConnectorClientMessageFile> getFiles() {
		return files;
	}


	public void setFiles(List<DomibusConnectorClientMessageFile> files) {
		this.files = files;
	}

}
