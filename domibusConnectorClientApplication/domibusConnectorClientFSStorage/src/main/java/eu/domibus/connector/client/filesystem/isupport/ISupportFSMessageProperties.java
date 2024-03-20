package eu.domibus.connector.client.filesystem.isupport;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;

@ConfigurationProperties(prefix = ISupportFSMessageProperties.PREFIX)
@Validated
@Valid
public class ISupportFSMessageProperties {
	
	public static final String PREFIX = "connector-client.storage.filesystem.message-properties";

	@NotEmpty
	private String fileName;
	@NotEmpty
	private String service;
	@NotEmpty
	private String serviceType;
	@NotEmpty
	private String action;
	@NotEmpty
	private String fromPartyRole;
	@NotEmpty
	private String fromPartyIdType;
	@NotEmpty
	private String toPartyRole;
	@NotEmpty
	private String toPartyIdType;
	@NotEmpty
	private String iSupportIncomingDir;
	@NotEmpty
	private String iSupportOutgoingDir;
	@NotEmpty
	private String processedFileName;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getFromPartyRole() {
		return fromPartyRole;
	}
	public void setFromPartyRole(String fromPartyRole) {
		this.fromPartyRole = fromPartyRole;
	}
	public String getToPartyRole() {
		return toPartyRole;
	}
	public void setToPartyRole(String toPartyRole) {
		this.toPartyRole = toPartyRole;
	}
	public String getFromPartyIdType() {
		return fromPartyIdType;
	}
	public void setFromPartyIdType(String fromPartyIdType) {
		this.fromPartyIdType = fromPartyIdType;
	}
	public String getToPartyIdType() {
		return toPartyIdType;
	}
	public void setToPartyIdType(String toPartyIdType) {
		this.toPartyIdType = toPartyIdType;
	}
	public String getiSupportIncomingDir() {
		return iSupportIncomingDir;
	}
	public void setiSupportIncomingDir(String iSupportIncomingDir) {
		this.iSupportIncomingDir = iSupportIncomingDir;
	}
	public String getiSupportOutgoingDir() {
		return iSupportOutgoingDir;
	}
	public void setiSupportOutgoingDir(String iSupportOutgoingDir) {
		this.iSupportOutgoingDir = iSupportOutgoingDir;
	}
	public String getProcessedFileName() {
		return processedFileName;
	}
	public void setProcessedFileName(String processedFileName) {
		this.processedFileName = processedFileName;
	}

}
