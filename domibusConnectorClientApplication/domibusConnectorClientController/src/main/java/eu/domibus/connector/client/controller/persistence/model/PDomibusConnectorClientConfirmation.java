package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;

@Entity
@Table(name = "CONNECTOR_CLIENT_CONFIRMATION")
public class PDomibusConnectorClientConfirmation {

	@Id
    @Column(name="ID")
	@SequenceGenerator(name = "clientConfirmationSeqGen", sequenceName = "CLIENT_CONFIRMATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "clientConfirmationSeqGen")
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorClientMessage message;
	
	@Column(name = "CONFIRMATION_TYPE", length = 255, nullable = false)
    private String confirmationType;
	
	@Column(name = "RECEIVED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date received;
	
//	@Column(name = "STORAGE_STATUS", length = 255)
//	@Enumerated(EnumType.STRING)
//    private DomibusConnectorClientStorageStatus storageStatus;
//	
//	@Column(name = "STORAGE_INFO", length = 255)
//    private String storageInfo;
	
	public PDomibusConnectorClientConfirmation() {
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public PDomibusConnectorClientMessage getMessage() {
		return message;
	}

	public void setMessage(PDomibusConnectorClientMessage message) {
		this.message = message;
	}

	public String getConfirmationType() {
		return confirmationType;
	}

	public void setConfirmationType(String confirmationType) {
		this.confirmationType = confirmationType;
	}

	public Date getReceived() {
		return received;
	}

	public void setReceived(Date received) {
		this.received = received;
	}

//	public DomibusConnectorClientStorageStatus getStorageStatus() {
//		return storageStatus;
//	}
//
//	public void setStorageStatus(DomibusConnectorClientStorageStatus storageStatus) {
//		this.storageStatus = storageStatus;
//	}
//
//	public String getStorageInfo() {
//		return storageInfo;
//	}
//
//	public void setStorageInfo(String storageInfo) {
//		this.storageInfo = storageInfo;
//	}
//	
	

}
