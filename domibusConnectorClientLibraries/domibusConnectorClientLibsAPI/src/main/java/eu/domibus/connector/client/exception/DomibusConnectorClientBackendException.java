/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.exception;

import lombok.NoArgsConstructor;

/**
 * DomibusConnectorClientBackendException is a custom exception class that extends the Exception
 * class. It represents an exception that occurs in the backend of the DomibusConnectorClient. This
 * exception can be thrown when there is an error in the backend operations of the client, such as
 * checking for new messages or delivering messages to the client backend.
 */
@NoArgsConstructor
public class DomibusConnectorClientBackendException extends Exception {
    private static final long serialVersionUID = 1L;

    public DomibusConnectorClientBackendException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorClientBackendException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorClientBackendException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorClientBackendException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
