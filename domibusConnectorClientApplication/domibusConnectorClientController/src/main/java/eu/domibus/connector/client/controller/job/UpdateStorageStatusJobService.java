package eu.domibus.connector.client.controller.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;

@Component
@Validated
@Valid
public class UpdateStorageStatusJobService {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UpdateStorageStatusJobService.class);

	@Autowired
	@NotNull
	private DomibusConnectorClientStorage storage;

	@Autowired
	@NotNull
	private IDomibusConnectorClientPersistenceService persistenceService;

	
	public UpdateStorageStatusJobService() {
	}

	public void checkStorageAndUpdateDatabaseMessages() {

		LocalDateTime startTime = LocalDateTime.now();
		LOGGER.debug("UpdateStorageStatusJobService started");

		Iterable<PDomibusConnectorClientMessage> allMessages = persistenceService.getMessageDao().findAll();

		allMessages.forEach(message -> {
			DomibusConnectorClientStorageStatus status = checkStorageStatus(message.getStorageInfo());
			
			if(message.getStorageStatus()==null || !message.getStorageStatus().equals(status)) {
				message.setStorageStatus(status);
				persistenceService.mergeClientMessage(message);
				LOGGER.info("StorageStatus for clientMessage with database id {} updated to {}!", message.getId(), status);
			}
			
		});

		LOGGER.debug("UpdateStorageStatusJobService finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
	}
	
	private DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation) {
		DomibusConnectorClientStorageStatus status = null;
		if(storageLocation != null && !storageLocation.isEmpty()) {
			status = storage.checkStorageStatus(storageLocation);
		}else {
			//No storageInfo in database available -> Status UNKNOWN
			status = DomibusConnectorClientStorageStatus.UNKNOWN;
		}

		if(status==null) {
			status = DomibusConnectorClientStorageStatus.UNKNOWN;
		}
		return status;
	}
}
