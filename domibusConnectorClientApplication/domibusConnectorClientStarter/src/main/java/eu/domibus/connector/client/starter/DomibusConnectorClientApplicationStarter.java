/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.starter;

import java.io.File;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

/**
 * Main entry point for the Domibus Connector Client Application. This class initializes and
 * configures the Spring Boot application context for the Domibus Connector Client, and is
 * responsible for starting the application.
 */
@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class DomibusConnectorClientApplicationStarter extends SpringBootServletInitializer {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientApplicationStarter.class);
    public static final String CONNECTOR_CLIENT_CONFIG_FILE = "connector-client.properties";
    public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
    public static final String SPRING_CONFIG_NAME = "connector-client";

    public static void main(String[] args) {
        runSpringApplication(args);
    }

    /**
     * Runs the Spring application for the Domibus Connector Client application. Configures the
     * application context with the necessary properties and settings before starting the
     * application.
     *
     * @param args The command-line arguments passed to the application.
     * @return The configured {@link ConfigurableApplicationContext} instance.
     */
    public static ConfigurableApplicationContext runSpringApplication(String[] args) {
        var builder = new SpringApplicationBuilder();
        builder = configureApplicationContext(builder);
        var springApplication = builder.build();
        return springApplication.run(args);
    }

    /**
     * Configures the application context for the Domibus Connector Client application. It sets
     * Spring properties based on the connector configuration file, if available.
     *
     * @param application The {@link SpringApplicationBuilder} instance to configure.
     * @return The configured {@link SpringApplicationBuilder} instance.
     */
    public static SpringApplicationBuilder configureApplicationContext(
        SpringApplicationBuilder application) {
        var connectorConfigFile = getConnectorConfigFile();
        var springProperties = new Properties();
        if (connectorConfigFile != null) {
            int lastIndex = connectorConfigFile.contains(File.separator)
                ? connectorConfigFile.lastIndexOf(File.separatorChar)
                : connectorConfigFile.lastIndexOf("/");
            lastIndex++;
            var configName = connectorConfigFile.substring(lastIndex);

            LOGGER.info("Setting:\n{}={}\n{}={}",
                        SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile,
                        SPRING_CONFIG_NAME_PROPERTY_NAME, configName
            );

            springProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);
            springProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, configName);
        } else {
            LOGGER.warn(
                "SystemProperty \"{}\" not given or not resolvable! Startup using default spring "
                    + "external configuration!",
                CONNECTOR_CLIENT_CONFIG_FILE
            );
            springProperties.setProperty(
                SPRING_CONFIG_NAME_PROPERTY_NAME,
                SPRING_CONFIG_NAME
            ); // look for <SPRING_CONFIG_NAME>.properties in config location
        }
        application.properties(
            springProperties); // pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorClientApplicationStarter.class);
    }

    /**
     * Retrieves the connector configuration file path from the system properties and resolves any
     * placeholders within the path.
     *
     * @return The resolved connector configuration file path if specified, otherwise null.
     */
    public static @Nullable
    String getConnectorConfigFile() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CLIENT_CONFIG_FILE);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }
}
