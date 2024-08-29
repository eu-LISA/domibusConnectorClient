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
 * The DCCMessageValidationException class is an exception that is thrown when there is an error
 * during message validation in a Domibus Connector Client. This exception is a subclass of
 * DomibusConnectorClientException.
 */
@NoArgsConstructor
public class DCCMessageValidationException extends DomibusConnectorClientException {
    /**
     * Constructor.
     *
     * @param message            the detail message (which is saved for later retrieval by the
     *                           {@link Throwable#getMessage()} method).
     * @param cause              the cause (which is saved for later retrieval by the
     *                           {@link Throwable#getCause()} method). (A null value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public DCCMessageValidationException(
        String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructor.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). (A null value is permitted, and
     *                indicates that the cause is nonexistent or unknown.)
     */
    public DCCMessageValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method).
     */
    public DCCMessageValidationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause the cause of the exception (A null value is permitted, and indicates that the
     *              cause is nonexistent or unknown.)
     */
    public DCCMessageValidationException(Throwable cause) {
        super(cause);
    }
}
