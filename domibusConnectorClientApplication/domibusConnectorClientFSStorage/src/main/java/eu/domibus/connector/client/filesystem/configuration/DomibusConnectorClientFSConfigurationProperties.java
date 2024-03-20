package eu.domibus.connector.client.filesystem.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = DomibusConnectorClientFSConfigurationProperties.PREFIX)
@Validated
@Valid
public class DomibusConnectorClientFSConfigurationProperties {
	
	public static final String PREFIX = "connector-client.storage.filesystem.properties";

	@NotEmpty
	private String messageReadyPostfix;
	
	@NotEmpty
	private String xmlFileExtension;
	
	@NotEmpty
	private String pdfFileExtension;
	
	@NotEmpty
	private String pkcs7FileExtension;
	
	@NotEmpty
	private String attachmentIdPrefix;
	
	@NotEmpty
	private String defaultPdfFileName;
	
	@NotEmpty
	private String defaultXmlFileName;
	
	@NotEmpty
	private String defaultDetachedSignatureFileName;
	
	
	public String getMessageReadyPostfix() {
		return messageReadyPostfix;
	}
	public void setMessageReadyPostfix(String messageReadyPostfix) {
		this.messageReadyPostfix = messageReadyPostfix;
	}
	public String getXmlFileExtension() {
		return xmlFileExtension;
	}
	public void setXmlFileExtension(String xmlFileExtension) {
		this.xmlFileExtension = xmlFileExtension;
	}
	public String getPdfFileExtension() {
		return pdfFileExtension;
	}
	public void setPdfFileExtension(String pdfFileExtension) {
		this.pdfFileExtension = pdfFileExtension;
	}
	public String getPkcs7FileExtension() {
		return pkcs7FileExtension;
	}
	public void setPkcs7FileExtension(String pkcs7FileExtension) {
		this.pkcs7FileExtension = pkcs7FileExtension;
	}
	public String getAttachmentIdPrefix() {
		return attachmentIdPrefix;
	}
	public void setAttachmentIdPrefix(String attachmentIdPrefix) {
		this.attachmentIdPrefix = attachmentIdPrefix;
	}
	public String getDefaultPdfFileName() {
		return defaultPdfFileName;
	}
	public void setDefaultPdfFileName(String defaultPdfFileName) {
		this.defaultPdfFileName = defaultPdfFileName;
	}
	public String getDefaultXmlFileName() {
		return defaultXmlFileName;
	}
	public void setDefaultXmlFileName(String defaultXmlFileName) {
		this.defaultXmlFileName = defaultXmlFileName;
	}
	public String getDefaultDetachedSignatureFileName() {
		return defaultDetachedSignatureFileName;
	}
	public void setDefaultDetachedSignatureFileName(String defaultDetachedSignatureFileName) {
		this.defaultDetachedSignatureFileName = defaultDetachedSignatureFileName;
	}
	
	
}
