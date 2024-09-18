/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.war;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

/**
 * DomibusConnectorClientWAR is the main entry point for the connector client application.
 */
@SpringBootApplication(scanBasePackages = "eu.ecodex.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class DomibusConnectorClientWAR extends SpringBootServletInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWAR.class);
    public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
    public static final String CONNECTOR_CONFIG_FILE_PROPERTY_NAME = "connector-client.config.file";
    public static final String DEFAULT_SPRING_CONFIG_NAME = "connector-client";
    public static final String DEFAULT_SPRING_CONFIG_LOCATION = "classpath:/config/,file:./conf/,"
        + "file:./conf/connector-client/,file:./config/,file:./config/connector-client/";
    String connectorConfigFile = null;
    String springConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String springConfigName = DEFAULT_SPRING_CONFIG_NAME;
    Properties springApplicationProperties = new Properties();

    /**
     * Configures the application context with the necessary configuration properties by setting the
     * Spring configuration location and name based on the specified connector configuration file or
     * system properties.
     *
     * @param application the Spring application builder used to configure the application context
     * @return the updated Spring application builder with the configured properties
     */
    public SpringApplicationBuilder configureApplicationContext(
        SpringApplicationBuilder application) {

        if (connectorConfigFile == null) {
            // connectorConfigFile not initialized by servletContext init parameter...
            // try to read as system parameter
            connectorConfigFile = getConnectorConfigFileSystemProperty();
        }

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

            springApplicationProperties.setProperty(
                SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);
            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, configName);
        } else {
            springApplicationProperties.setProperty(
                SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
            springApplicationProperties.setProperty(
                SPRING_CONFIG_NAME_PROPERTY_NAME, springConfigName);
            LOGGER.warn(
                "No \"{}\" parameter given or resolveable! Startup using default spring external "
                    + "configuration.",
                CONNECTOR_CONFIG_FILE_PROPERTY_NAME
            );
        }
        LOGGER.info(
            "Setting:\n{}={}\n{}={}",
            SPRING_CONFIG_LOCATION_PROPERTY_NAME,
            springApplicationProperties.get(SPRING_CONFIG_LOCATION_PROPERTY_NAME),
            SPRING_CONFIG_NAME_PROPERTY_NAME,
            springApplicationProperties.get(SPRING_CONFIG_NAME_PROPERTY_NAME)
        );
        application.properties(
            springApplicationProperties
        ); // pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorClientWAR.class);
    }

    /**
     * Retrieves the connector configuration file system property.
     *
     * <p>This method fetches the system property defined by the
     * `CONNECTOR_CONFIG_FILE_PROPERTY_NAME`.
     * If the system property is set, it resolves any placeholders within the property value.
     *
     * @return the resolved path of the connector configuration file if the system property is set;
     *      otherwise, returns {@code null}.
     */
    public static @Nullable
    String getConnectorConfigFileSystemProperty() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }

    /***
     * {@inheritDoc}
     *
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return configureApplicationContext(application);
    }

    /**
     * Loads the connector configuration properties from the specified file.
     *
     * @param connectorConfigFile the path to the configuration file. If {@code null}, or if the
     *                            file does not exist, an empty {@code Properties} object will be
     *                            returned or a {@code RuntimeException} will be thrown
     *                            respectively.
     * @return the loaded {@code Properties} object containing the configuration properties. If the
     *      {@code connectorConfigFile} is {@code null}, returns an empty {@code Properties} object.
     *      If the file cannot be read, throws a {@code RuntimeException}.
     * @throws RuntimeException if the specified configuration file does not exist or cannot be
     *                          read.
     */
    public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
        var properties = new Properties();
        if (connectorConfigFile != null) {
            var connectorConfigFilePath = Paths.get(connectorConfigFile);
            if (!Files.exists(connectorConfigFilePath)) {
                var errorString = String.format(
                    "Cannot start because the via System Property [%s] provided config file [%s] "
                        + "mapped to path [%s] does not exist!",
                    CONNECTOR_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile,
                    connectorConfigFilePath
                );
                LOGGER.error(errorString);
                throw new RuntimeException(errorString);
            }
            try {

                var fileInputStream = new FileInputStream(connectorConfigFilePath.toFile());
                properties.load(fileInputStream);
                fileInputStream.close();
                return properties;
            } catch (IOException e) {
                throw new RuntimeException(String.format(
                    "Cannot load properties from file [%s], is it a valid and readable properties "
                        + "file?",
                    connectorConfigFilePath
                ), e);
            }
        }
        return properties;
    }

    /**
     * {@inheritDoc} Will only be called if the Application is deployed within an web application
     * server adds to the boostrap and spring config location search path a web application context
     * dependent search path: app deployed under context /connector will look also for config under
     * [workingpath]/config/[webcontext]/, [workingpath]/conf/[webcontext]/
     *
     * @param servletContext the servlet context
     * @throws ServletException in case of an error @see
     *                          {@link SpringBootServletInitializer#onStartup(ServletContext)}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (servletContext != null) {
            connectorConfigFile =
                servletContext.getInitParameter(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);

            if (connectorConfigFile == null) {
                // if connector config file not a parameter in servlet context, set the servlet
                // context path as spring config
                springConfigLocation = springConfigLocation
                    + ",file:./config/" + servletContext.getContextPath() + "/"
                    + ",file:./conf/" + servletContext.getContextPath() + "/";

                springConfigName = servletContext.getContextPath();
            }

            super.onStartup(servletContext);
        }
    }
}
