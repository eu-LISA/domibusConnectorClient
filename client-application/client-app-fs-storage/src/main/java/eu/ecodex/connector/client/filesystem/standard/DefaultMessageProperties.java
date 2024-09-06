/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.standard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DefaultMessageProperties class represents a set of properties associated with a message. It
 * is used to load properties from a file, store properties to a file, and retrieve the message
 * properties.
 */
@Getter
@NoArgsConstructor
public class DefaultMessageProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageProperties.class);
    private final Properties messageProperties = new Properties();

    public Properties getMessageProperties() {
        return messageProperties;
    }

    /**
     * The DefaultMessageProperties class represents a set of properties associated with a message.
     * It is used to load properties from a file, store properties to a file, and retrieve the
     * message properties.
     */
    public DefaultMessageProperties(File message, String messagePropertiesFileName) {
        String pathname = message.getAbsolutePath() + File.separator + messagePropertiesFileName;
        LOGGER.debug("Loading message properties from file {}", pathname);
        var messagePropertiesFile = new File(pathname);
        if (!messagePropertiesFile.exists()) {
            LOGGER.error(
                "Message properties file '{}' does not exist. Message cannot be processed!",
                messagePropertiesFile.getAbsolutePath()
            );
        }
        loadPropertiesFromFile(messagePropertiesFile);
    }

    /**
     * Loads properties from a specified file into the messageProperties object.
     *
     * @param messagePropertiesFile The file from which to load the properties.
     * @throws RuntimeException if an I/O error occurs while loading the properties.
     */
    public void loadPropertiesFromFile(File messagePropertiesFile) {
        try (var fileInputStream = new FileInputStream(messagePropertiesFile)) {
            messageProperties.load(fileInputStream);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * Stores the message properties to a file.
     *
     * @param messagePropertiesFile The file to which the message properties will be stored.
     * @throws RuntimeException if an I/O error occurs while storing the properties.
     */
    public void storeMessagePropertiesToFile(File messagePropertiesFile) {
        if (!messagePropertiesFile.exists()) {
            try {
                messagePropertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (var fos = new FileOutputStream(messagePropertiesFile)) {
            messageProperties.store(fos, null);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }
}
