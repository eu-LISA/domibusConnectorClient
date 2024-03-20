package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;

@Entity
@Table(name = "CONNECTOR_CLIENT_MESSAGE")
public class PDomibusConnectorClientMessage {

	@Id
    @Column(name="ID")
	@SequenceGenerator(name = "clientMessageSeqGen", sequenceName = "CLIENT_MESSAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "clientMessageSeqGen")
	private Long id;
	
	@Column(name = "EBMS_MESSAGE_ID", length = 255, unique = true)
	private String ebmsMessageId;
	
	@Column(name = "BACKEND_MESSAGE_ID", unique = true, length = 255)
    private String backendMessageId;
	
	@Column(name = "CONVERSATION_ID", length = 255)
    private String conversationId;
	
	@Column(name = "ORIGINAL_SENDER", length = 255)
    private String originalSender;
	
	@Column(name = "FINAL_RECIPIENT", length = 255)
    private String finalRecipient;
	
	@Column(name = "FROM_PARTY_ID", length = 255)
    private String fromPartyId;
	
	@Column(name = "FROM_PARTY_TYPE", length = 255)
    private String fromPartyType;
	
	@Column(name = "FROM_PARTY_ROLE", length = 255)
    private String fromPartyRole;
	
	@Column(name = "TO_PARTY_ID", length = 255)
    private String toPartyId;
	
	@Column(name = "TO_PARTY_TYPE", length = 255)
    private String toPartyType;
	
	@Column(name = "TO_PARTY_ROLE", length = 255)
    private String toPartyRole;
	
	@Column(name = "SERVICE", length = 255)
    private String service;

	@Column(name = "SERVICE_TYPE", length = 512)
    private String serviceType;
	
	@Column(name = "ACTION", length = 255)
    private String action;
		
	@Column(name = "STORAGE_STATUS", length = 255)
	@Enumerated(EnumType.STRING)
    private DomibusConnectorClientStorageStatus storageStatus;
	
	@Column(name = "STORAGE_INFO", length = 255)
    private String storageInfo;
	
	@Column(name = "LAST_CONFIRMATION_RECEIVED", length = 255)
    private String lastConfirmationReceived;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "MESSAGE_STATUS", length = 255)
	@Enumerated(EnumType.STRING)
	private PDomibusConnectorClientMessageStatus messageStatus;
	
	@OneToMany(mappedBy = "message", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorClientConfirmation> confirmations = new HashSet<>();
	
	public PDomibusConnectorClientMessage() {
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

	public DomibusConnectorClientStorageStatus getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(DomibusConnectorClientStorageStatus storageStatus) {
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}


	public PDomibusConnectorClientMessageStatus getMessageStatus() {
		return messageStatus;
	}



	public void setMessageStatus(PDomibusConnectorClientMessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}



	public Set<PDomibusConnectorClientConfirmation> getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(Set<PDomibusConnectorClientConfirmation> confirmations) {
		this.confirmations = confirmations;
	}

}
