/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code DomibusConnectorClientMessageFile} class represents a file associated with a message
 * that is used by the Domibus connector client.
 *
 * @see DomibusConnectorClientMessageFileType
 * @see DomibusConnectorClientMessageFileList
 */
@Data
@NoArgsConstructor
public class DomibusConnectorClientMessageFile {
    private String fileName;
    private DomibusConnectorClientMessageFileType fileType;
    private byte[] fileContent;
    private String storageLocation;

    public DomibusConnectorClientMessageFile(
        String name, DomibusConnectorClientMessageFileType type) {
        this.fileName = name;
        this.setFileType(type);
    }

    /**
     * Constructor.
     *
     * @see DomibusConnectorClientMessageFileType
     * @see DomibusConnectorClientMessageFileList
     */
    public DomibusConnectorClientMessageFile(
        String name, DomibusConnectorClientMessageFileType type, byte[] content) {
        this.fileName = name;
        this.setFileType(type);
        this.setFileContent(content);
    }
}
