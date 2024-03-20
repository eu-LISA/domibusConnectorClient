package eu.domibus.connector.client.rest.model;


public class DomibusConnectorClientMessageFile {

	private String fileName;
	
	private DomibusConnectorClientMessageFileType fileType;
	
	private byte[] fileContent;
	
	private String storageLocation;
	
	public DomibusConnectorClientMessageFile() {
		// TODO Auto-generated constructor stub
	}
	
	public DomibusConnectorClientMessageFile(String name, DomibusConnectorClientMessageFileType type) {
		this.fileName = name;
		this.setFileType(type);
	}
	
	public DomibusConnectorClientMessageFile(String name, DomibusConnectorClientMessageFileType type, byte[] content) {
		this.fileName = name;
		this.setFileType(type);
		this.setFileContent(content);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public DomibusConnectorClientMessageFileType getFileType() {
		return fileType;
	}

	public void setFileType(DomibusConnectorClientMessageFileType fileType) {
		this.fileType = fileType;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	

}
