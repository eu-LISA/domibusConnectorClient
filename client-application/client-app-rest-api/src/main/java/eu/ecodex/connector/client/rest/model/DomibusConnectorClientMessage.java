/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.rest.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code DomibusConnectorClientMessage} class represents a message in the Domibus Connector
 * Client.
 *
 * @see DomibusConnectorClientConfirmation
 * @see DomibusConnectorClientMessageFileList
 */
@Data
@NoArgsConstructor
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
    private DomibusConnectorClientMessageFileList files =
        new DomibusConnectorClientMessageFileList();
}
