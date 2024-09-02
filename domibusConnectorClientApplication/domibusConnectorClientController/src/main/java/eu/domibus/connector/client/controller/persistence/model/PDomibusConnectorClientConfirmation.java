/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a confirmation received by the connector client from a
 * PDomibusConnectorClientMessage.
 */
@Data
@Entity
@Table(name = "CONNECTOR_CLIENT_CONFIRMATION")
@NoArgsConstructor
public class PDomibusConnectorClientConfirmation {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(
        name = "clientConfirmationSeqGen", sequenceName = "CLIENT_CONFIRMATION_SEQ",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientConfirmationSeqGen")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorClientMessage message;
    @Column(name = "CONFIRMATION_TYPE", nullable = false)
    private String confirmationType;
    @Column(name = "RECEIVED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date received;
}
