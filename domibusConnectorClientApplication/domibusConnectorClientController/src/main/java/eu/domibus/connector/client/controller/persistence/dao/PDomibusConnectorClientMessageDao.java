package eu.domibus.connector.client.controller.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;

@Repository
public interface PDomibusConnectorClientMessageDao extends CrudRepository<PDomibusConnectorClientMessage, Long> {

	public List<PDomibusConnectorClientMessage> findByConversationId(String conversationId);
	
	@Query("SELECT m FROM PDomibusConnectorClientMessage m WHERE "
			+ "(m.created is not null AND m.created between ?1 and ?2)")
    public List<PDomibusConnectorClientMessage> findByPeriod(Date from, Date to);
	
	@Query("SELECT m FROM PDomibusConnectorClientMessage m WHERE "
			+ "m.messageStatus='RECEIVED'")
    public List<PDomibusConnectorClientMessage> findReceived();
	
	@Query("SELECT m FROM PDomibusConnectorClientMessage m WHERE "
			+ "m.messageStatus='REJECTED' or m.messageStatus='CONFIRMED'")
    public List<PDomibusConnectorClientMessage> findRejectedConfirmed();
	
	
	public Optional<PDomibusConnectorClientMessage> findOneByEbmsMessageIdAndBackendMessageId(String ebmsMessageId, String backendId);
	
	public Optional<PDomibusConnectorClientMessage> findOneByStorageInfo(String storageInfo);
	
	public Optional<PDomibusConnectorClientMessage> findOneByBackendMessageId(String backendId);
	
	public Optional<PDomibusConnectorClientMessage> findOneByEbmsMessageId(String ebmsMessageId);
	
}
