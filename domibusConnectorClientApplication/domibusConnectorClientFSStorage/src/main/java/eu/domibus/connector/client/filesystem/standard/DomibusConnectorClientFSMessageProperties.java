/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem.standard;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The DomibusConnectorClientFSMessageProperties class represents the configuration properties for
 * the file system storage of the Domibus connector client. It is used to set and retrieve various
 * properties related to message files stored in the file system.
 */
@Data
@Valid
@Validated
@ConfigurationProperties(prefix = DomibusConnectorClientFSMessageProperties.PREFIX)
public class DomibusConnectorClientFSMessageProperties {
    public static final String PREFIX = "connector-client.storage.filesystem.message-properties";
    @NotEmpty
    private String fileName;
    @NotEmpty
    private String service;
    @NotEmpty
    private String serviceType;
    @NotEmpty
    private String action;
    @NotEmpty
    private String toPartyRole;
    @NotEmpty
    private String toPartyId;
    @NotEmpty
    private String fromPartyRole;
    @NotEmpty
    private String fromPartyId;
    @NotEmpty
    private String originalSender;
    @NotEmpty
    private String finalRecipient;
    @NotEmpty
    private String backendMessageId;
    @NotEmpty
    private String ebmsMessageId;
    @NotEmpty
    private String conversationId;
    @NotEmpty
    private String contentPdfFileName;
    @NotEmpty
    private String contentXmlFileName;
    @NotEmpty
    private String detachedSignatureFileName;
    @NotEmpty
    private String messageReceivedDatetime;
    @NotEmpty
    private String messageSentDatetime;
}
