/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * This class represents the configuration properties for the Domibus connector client file system
 * storage.
 */
@ConfigurationProperties(prefix = DomibusConnectorClientFSConfigurationProperties.PREFIX)
@Validated
@Valid
@Data
public class DomibusConnectorClientFSConfigurationProperties {
    public static final String PREFIX = "connector-client.storage.filesystem.properties";
    @NotEmpty
    private String messageReadyPostfix;
    @NotEmpty
    private String xmlFileExtension;
    @NotEmpty
    private String pdfFileExtension;
    @NotEmpty
    private String pkcs7FileExtension;
    @NotEmpty
    private String attachmentIdPrefix;
    @NotEmpty
    private String defaultPdfFileName;
    @NotEmpty
    private String defaultXmlFileName;
    @NotEmpty
    private String defaultDetachedSignatureFileName;
}
