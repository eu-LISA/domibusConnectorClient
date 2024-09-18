/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.persistence.model;

import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a message sent by a Domibus connector client.
 *
 * @see DomibusConnectorClientStorageStatus
 * @see PDomibusConnectorClientMessageStatus
 * @see PDomibusConnectorClientConfirmation
 */
@Data
@Entity
@Table(name = "CONNECTOR_CLIENT_MESSAGE")
@NoArgsConstructor
public class PDomibusConnectorClientMessage {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(
        name = "clientMessageSeqGen", sequenceName = "CLIENT_MESSAGE_SEQ", allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientMessageSeqGen")
    private Long id;
    @Column(name = "EBMS_MESSAGE_ID", unique = true)
    private String ebmsMessageId;
    @Column(name = "BACKEND_MESSAGE_ID", unique = true)
    private String backendMessageId;
    @Column(name = "CONVERSATION_ID")
    private String conversationId;
    @Column(name = "ORIGINAL_SENDER")
    private String originalSender;
    @Column(name = "FINAL_RECIPIENT")
    private String finalRecipient;
    @Column(name = "FROM_PARTY_ID")
    private String fromPartyId;
    @Column(name = "FROM_PARTY_TYPE")
    private String fromPartyType;
    @Column(name = "FROM_PARTY_ROLE")
    private String fromPartyRole;
    @Column(name = "TO_PARTY_ID")
    private String toPartyId;
    @Column(name = "TO_PARTY_TYPE")
    private String toPartyType;
    @Column(name = "TO_PARTY_ROLE")
    private String toPartyRole;
    @Column(name = "SERVICE")
    private String service;
    @Column(name = "SERVICE_TYPE", length = 512)
    private String serviceType;
    @Column(name = "ACTION")
    private String action;
    @Column(name = "STORAGE_STATUS")
    @Enumerated(EnumType.STRING)
    private DomibusConnectorClientStorageStatus storageStatus;
    @Column(name = "STORAGE_INFO")
    private String storageInfo;
    @Column(name = "LAST_CONFIRMATION_RECEIVED")
    private String lastConfirmationReceived;
    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "MESSAGE_STATUS")
    @Enumerated(EnumType.STRING)
    private PDomibusConnectorClientMessageStatus messageStatus;
    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorClientConfirmation> confirmations = new HashSet<>();
}
