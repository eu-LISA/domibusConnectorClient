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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * The `DomibusConnectorClientMessageFileList` class represents a list of files associated with a
 * message that is used by the Domibus connector client.
 *
 * @see DomibusConnectorClientMessageFile
 */
@Data
public class DomibusConnectorClientMessageFileList {
    private List<DomibusConnectorClientMessageFile> files;

    public DomibusConnectorClientMessageFileList() {
        setFiles(new ArrayList<>());
    }

    public void add(DomibusConnectorClientMessageFile file) {
        getFiles().add(file);
    }
}
