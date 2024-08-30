/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem;

import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import java.io.Serial;
import lombok.NoArgsConstructor;

/**
 * Represents a file system exception that occurs in the Domibus Connector Client.
 * This exception is thrown when there is an error related to the file system, such as
 * storing or retrieving data in the file system.
 *
 * @see DomibusConnectorClientStorageException
 * @since 1.0
 */
@NoArgsConstructor
public class DomibusConnectorClientFileSystemException
    extends DomibusConnectorClientStorageException {
    @Serial
    private static final long serialVersionUID = -4383246883598401524L;

    public DomibusConnectorClientFileSystemException(String message) {
        super(message);
    }

    public DomibusConnectorClientFileSystemException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorClientFileSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomibusConnectorClientFileSystemException(
        String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
