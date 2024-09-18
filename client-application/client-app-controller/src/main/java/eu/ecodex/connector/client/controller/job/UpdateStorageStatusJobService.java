/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.job;

import eu.ecodex.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.ecodex.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * This class is responsible for checking the storage status of messages in the database and
 * updating the storage status accordingly. It uses the DomibusConnectorClientStorage and
 * IDomibusConnectorClientPersistenceService interfaces for accessing the storage and the database,
 * respectively.
 */
@Data
@Valid
@Component
@Validated
@NoArgsConstructor
public class UpdateStorageStatusJobService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UpdateStorageStatusJobService.class);
    @Autowired
    @NotNull
    private DomibusConnectorClientStorage storage;
    @Autowired
    @NotNull
    private IDomibusConnectorClientPersistenceService persistenceService;

    /**
     * Method to check the storage status of messages in the database and update the storage status
     * accordingly.
     *
     * @see UpdateStorageStatusJobService
     * @see PDomibusConnectorClientMessage
     * @see DomibusConnectorClientStorageStatus
     */
    public void checkStorageAndUpdateDatabaseMessages() {
        var startTime = LocalDateTime.now();
        LOGGER.debug("UpdateStorageStatusJobService started");

        Iterable<PDomibusConnectorClientMessage> allMessages =
            persistenceService.getMessageDao().findAll();

        allMessages.forEach(message -> {
            var status = checkStorageStatus(message.getStorageInfo());

            if (message.getStorageStatus() == null || !message.getStorageStatus().equals(status)) {
                message.setStorageStatus(status);
                persistenceService.mergeClientMessage(message);
                LOGGER.info(
                    "StorageStatus for clientMessage with database id {} updated to {}!",
                    message.getId(), status
                );
            }
        });

        LOGGER.debug(
            "UpdateStorageStatusJobService finished after [{}]",
            Duration.between(startTime, LocalDateTime.now())
        );
    }

    private DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation) {
        DomibusConnectorClientStorageStatus status;
        if (storageLocation != null && !storageLocation.isEmpty()) {
            status = storage.checkStorageStatus(storageLocation);
        } else {
            // No storageInfo in database available -> Status UNKNOWN
            status = DomibusConnectorClientStorageStatus.UNKNOWN;
        }

        if (status == null) {
            status = DomibusConnectorClientStorageStatus.UNKNOWN;
        }
        return status;
    }
}
