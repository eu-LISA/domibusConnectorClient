/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.storage.exception;

import lombok.NoArgsConstructor;

/**
 * Signals a storage exception in the Domibus Connector Client. This exception is thrown when there
 * is an error related to storing or retrieving data in the Domibus Connector Client storage.
 */
@NoArgsConstructor
public class DomibusConnectorClientStorageException extends Exception {
    private static final long serialVersionUID = 1L;

    public DomibusConnectorClientStorageException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorClientStorageException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorClientStorageException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorClientStorageException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
