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

import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFSStorage;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFSStorageImpl;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemWriter;
import eu.ecodex.connector.client.filesystem.standard.reader.DefaultFSReaderImpl;
import eu.ecodex.connector.client.filesystem.standard.writer.DefaultFSWriterImpl;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * The DomibusConnectorClientFSStorageConfiguration class is a configuration class for the Domibus
 * connector client file system storage. It provides beans for the DomibusConnectorClientStorage,
 * DomibusConnectorClientFileSystemReader, and DomibusConnectorClientFileSystemWriter.
 */
@Configuration
@ConditionalOnProperty(
    prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX,
    name = DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue = "true",
    matchIfMissing = true
)
@PropertySource("classpath:/connector-client-fs.properties")
@EnableConfigurationProperties(
    {DomibusConnectorClientFSConfigurationProperties.class,
        DirectoryConfigurationConfigurationProperties.class}
)
@Data
@NoArgsConstructor
public class DomibusConnectorClientFSStorageConfiguration {
    public static final String PREFIX = "connector-client.storage.filesystem";
    public static final String ENABLED_PROPERTY_NAME = "enabled";

    /**
     * Creates an instance of DomibusConnectorClientFSStorage.
     *
     * @param messages the configuration properties for the directory
     * @return an instance of DomibusConnectorClientFSStorage
     */
    @Bean
    public DomibusConnectorClientStorage domibusConnectorClientFSStorage(
        DirectoryConfigurationConfigurationProperties messages) {
        DomibusConnectorClientFSStorage fsStorage = new DomibusConnectorClientFSStorageImpl();

        fsStorage.setMessagesDir(messages.getPath().toFile());

        return fsStorage;
    }

    @Bean
    @ConditionalOnMissingBean({DomibusConnectorClientFileSystemReader.class})
    public DomibusConnectorClientFileSystemReader domibusConnectorClientFileSystemReader() {
        return new DefaultFSReaderImpl();
    }

    @Bean
    @ConditionalOnMissingBean({DomibusConnectorClientFileSystemWriter.class})
    public DomibusConnectorClientFileSystemWriter domibusConnectorClientFileSystemWriter() {
        return new DefaultFSWriterImpl();
    }
}
