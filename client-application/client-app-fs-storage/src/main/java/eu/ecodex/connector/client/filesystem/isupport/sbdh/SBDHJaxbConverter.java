/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.isupport.sbdh;

import eu.ecodex.connector.client.filesystem.isupport.sbdh.model.StandardBusinessDocumentHeader;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * The SBDHJaxbConverter class is responsible for converting XML files to
 * StandardBusinessDocumentHeader (SBDH) objects. It uses JAXB (Java Architecture for XML Binding)
 * for unmarshalling XML data into Java objects.
 */
@Component
@Validated
@Valid
@Profile("iSupport")
public class SBDHJaxbConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SBDHJaxbConverter.class);
    private Unmarshaller jaxbMarshaller;

    /**
     * This method retrieves the StandardBusinessDocumentHeader (SBDH) from the provided XML file.
     *
     * @param message                   the XML file containing the SBDH
     * @param messagePropertiesFileName the name of the SBDH file
     * @return the retrieved StandardBusinessDocumentHeader object, or null if the file does not
     *      exist
     * @throws JAXBException if there is an error while unmarshalling the XML
     * @throws IOException   if there is an error while reading the file
     */
    public StandardBusinessDocumentHeader getSBDH(File message, String messagePropertiesFileName)
        throws JAXBException, IOException {
        if (this.jaxbMarshaller == null) {
            createUnmarshaller();
        }

        String pathname = message.getAbsolutePath() + File.separator + messagePropertiesFileName;
        LOGGER.debug("#getSBDH: Loading SBDH from file {}", pathname);
        var file = new File(pathname);
        if (!file.exists()) {
            LOGGER.error(
                "#getSBDH: SBDH file '{}' does not exist. Message cannot be processed!",
                file.getAbsolutePath()
            );
            return null;
        }

        var fileInputStream = new FileInputStream(file);
        var data = new byte[(int) file.length()];
        if (fileInputStream.read(data) == 0) {
            LOGGER.trace("Data read OK");
        }
        fileInputStream.close();

        return (StandardBusinessDocumentHeader) jaxbMarshaller.unmarshal(new
                                                                             ByteArrayInputStream(
            new String(data).getBytes(StandardCharsets.UTF_8)));
    }

    private void createUnmarshaller() throws JAXBException {
        var jaxbContext = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
        this.jaxbMarshaller = jaxbContext.createUnmarshaller();
    }
}
