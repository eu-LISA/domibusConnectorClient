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
 * The DCCMessageDataInvalid class is a subclass of DomibusConnectorClientException and represents
 * an exception that occurs when there is invalid message data in the Domibus Connector Client.
 */
@NoArgsConstructor
public class DCCMessageDataInvalid extends DomibusConnectorClientException {
    public DCCMessageDataInvalid(String message) {
        super(message);
    }

    public DCCMessageDataInvalid(Throwable cause) {
        super(cause);
    }

    public DCCMessageDataInvalid(String message, Throwable cause) {
        super(message, cause);
    }

    public DCCMessageDataInvalid(
        String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
