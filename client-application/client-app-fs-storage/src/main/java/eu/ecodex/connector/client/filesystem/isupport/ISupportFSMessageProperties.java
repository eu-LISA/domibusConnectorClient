/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.isupport;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The ISupportFSMessageProperties class represents the configuration properties for the iSupport
 * file system storage within the Domibus system. It is used in the ISupportFSConfiguration class to
 * configure the iSupport file system storage.
 */
@Data
@Valid
@Validated
@SuppressWarnings("checkstyle:MemberName")
@ConfigurationProperties(prefix = ISupportFSMessageProperties.PREFIX)
public class ISupportFSMessageProperties {
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
    private String fromPartyRole;
    @NotEmpty
    private String fromPartyIdType;
    @NotEmpty
    private String toPartyRole;
    @NotEmpty
    private String toPartyIdType;
    @NotEmpty
    private String iSupportIncomingDir;
    @NotEmpty
    private String iSupportOutgoingDir;
    @NotEmpty
    private String processedFileName;
}
