/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.starter.exception;

import lombok.NoArgsConstructor;

/**
 * Signals an error specific to the Domibus Connector client starter.
 *
 * <p>This exception can be thrown in scenarios where starting the client encounters unexpected
 * issues.
 */
@NoArgsConstructor
public class DomibusConnectorClientStarterException extends Exception {
    public DomibusConnectorClientStarterException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorClientStarterException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorClientStarterException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorClientStarterException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
