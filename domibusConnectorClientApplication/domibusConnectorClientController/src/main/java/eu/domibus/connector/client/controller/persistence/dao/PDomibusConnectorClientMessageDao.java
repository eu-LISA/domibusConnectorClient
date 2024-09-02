/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.persistence.dao;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This class is a DAO (Data Access Object) for the PDomibusConnectorClientMessage entity.
 */
@Repository
public interface PDomibusConnectorClientMessageDao
    extends CrudRepository<PDomibusConnectorClientMessage, Long> {
    List<PDomibusConnectorClientMessage> findByConversationId(String conversationId);

    @Query(
        "SELECT m FROM PDomibusConnectorClientMessage m WHERE "
            + "(m.created is not null AND m.created between ?1 and ?2)"
    )
    List<PDomibusConnectorClientMessage> findByPeriod(Date from, Date to);

    @Query(
        "SELECT m FROM PDomibusConnectorClientMessage m WHERE "
            + "m.messageStatus='RECEIVED'"
    )
    List<PDomibusConnectorClientMessage> findReceived();

    @Query(
        "SELECT m FROM PDomibusConnectorClientMessage m WHERE "
            + "m.messageStatus='REJECTED' or m.messageStatus='CONFIRMED'"
    )
    List<PDomibusConnectorClientMessage> findRejectedConfirmed();

    Optional<PDomibusConnectorClientMessage> findOneByEbmsMessageIdAndBackendMessageId(
        String ebmsMessageId, String backendId);

    Optional<PDomibusConnectorClientMessage> findOneByStorageInfo(String storageInfo);

    Optional<PDomibusConnectorClientMessage> findOneByBackendMessageId(String backendId);

    Optional<PDomibusConnectorClientMessage> findOneByEbmsMessageId(String ebmsMessageId);
}
