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
 * The {@code DomibusConnectorClientException} class is a custom exception that is used to handle
 * errors that occur during the Domibus Connector Client operations.
 */
@NoArgsConstructor
public class DomibusConnectorClientException extends Exception {
    private static final long serialVersionUID = 260299312166946237L;

    public DomibusConnectorClientException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorClientException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorClientException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorClientException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
