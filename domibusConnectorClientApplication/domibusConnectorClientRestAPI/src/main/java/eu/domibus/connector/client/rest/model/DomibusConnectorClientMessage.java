package eu.domibus.connector.client.rest.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DomibusConnectorClientMessage {

	private Long id;
	
    private String ebmsMessageId;
	
    private String backendMessageId;
	
    private String conversationId;
    
    private String originalSender;
    
    private String finalRecipient;
	
    private String fromPartyId;
	
    private String fromPartyType;
	
    private String fromPartyRole;
	
    private String toPartyId;
	
    private String toPartyType;
	
    private String toPartyRole;
    
    private String service;

    private String serviceType;

    private String action;
	
    private String storageStatus;
	
    private String storageInfo;
	
    private String lastConfirmationReceived;
    
    private String messageStatus;
	
    private Date created;
	
    private Set<DomibusConnectorClientConfirmation> evidences = new HashSet<>();
    
    private DomibusConnectorClientMessageFileList files = new DomibusConnectorClientMessageFileList();

	/**
	 * 
	 */
	public DomibusConnectorClientMessage() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEbmsMessageId() {
		return ebmsMessageId;
	}

	public void setEbmsMessageId(String ebmsMessageId) {
		this.ebmsMessageId = ebmsMessageId;
	}

	public String getBackendMessageId() {
		return backendMessageId;
	}

	public void setBackendMessageId(String backendMessageId) {
		this.backendMessageId = backendMessageId;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getOriginalSender() {
		return originalSender;
	}

	public void setOriginalSender(String originalSender) {
		this.originalSender = originalSender;
	}

	public String getFinalRecipient() {
		return finalRecipient;
	}

	public void setFinalRecipient(String finalRecipient) {
		this.finalRecipient = finalRecipient;
	}

	public String getFromPartyId() {
		return fromPartyId;
	}

	public void setFromPartyId(String fromPartyId) {
		this.fromPartyId = fromPartyId;
	}

	public String getFromPartyType() {
		return fromPartyType;
	}

	public void setFromPartyType(String fromPartyType) {
		this.fromPartyType = fromPartyType;
	}

	public String getFromPartyRole() {
		return fromPartyRole;
	}

	public void setFromPartyRole(String fromPartyRole) {
		this.fromPartyRole = fromPartyRole;
	}

	public String getToPartyId() {
		return toPartyId;
	}

	public void setToPartyId(String toPartyId) {
		this.toPartyId = toPartyId;
	}

	public String getToPartyType() {
		return toPartyType;
	}

	public void setToPartyType(String toPartyType) {
		this.toPartyType = toPartyType;
	}

	public String getToPartyRole() {
		return toPartyRole;
	}

	public void setToPartyRole(String toPartyRole) {
		this.toPartyRole = toPartyRole;
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

	public String getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(String storageStatus) {
		this.storageStatus = storageStatus;
	}

	public String getStorageInfo() {
		return storageInfo;
	}

	public void setStorageInfo(String storageInfo) {
		this.storageInfo = storageInfo;
	}

	public String getLastConfirmationReceived() {
		return lastConfirmationReceived;
	}

	public void setLastConfirmationReceived(String lastConfirmationReceived) {
		this.lastConfirmationReceived = lastConfirmationReceived;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Set<DomibusConnectorClientConfirmation> getEvidences() {
		return evidences;
	}

	public void setEvidences(Set<DomibusConnectorClientConfirmation> evidences) {
		this.evidences = evidences;
	}

	public DomibusConnectorClientMessageFileList getFiles() {
		return files;
	}

	public void setFiles(DomibusConnectorClientMessageFileList files) {
		this.files = files;
	}

}
