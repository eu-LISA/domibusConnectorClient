/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.exception;

import lombok.NoArgsConstructor;

/**
 * This class is an exception that represents an error that occurred in the backend web service
 * client of the Domibus connector.
 *
 * @see DomibusConnectorClientException
 */
@NoArgsConstructor
public class DomibusConnectorBackendWebServiceClientException
    extends DomibusConnectorClientException {
    public DomibusConnectorBackendWebServiceClientException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorBackendWebServiceClientException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorBackendWebServiceClientException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorBackendWebServiceClientException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
