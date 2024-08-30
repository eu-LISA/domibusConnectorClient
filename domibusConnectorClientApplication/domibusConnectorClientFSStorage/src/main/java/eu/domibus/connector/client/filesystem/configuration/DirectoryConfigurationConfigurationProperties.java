/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckFolderWriteable;
import java.nio.file.Path;
import javax.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The DirectoryConfigurationConfigurationProperties class represents the configuration properties
 * for the directory configuration of the connector client.
 */
@ConfigurationProperties(prefix = DirectoryConfigurationConfigurationProperties.PREFIX)
@Validated
@Valid
@Data
@NoArgsConstructor
public class DirectoryConfigurationConfigurationProperties {
    public static final String PREFIX = "connector-client.storage.filesystem.messages";
    @CheckFolderWriteable
    private Path path;
    private boolean createIfNonExistent;

    @Override
    public String toString() {
        return "DirectoryConfigurationProperties [path=" + path + ", createIfNonExistent="
            + createIfNonExistent + "]";
    }
}
