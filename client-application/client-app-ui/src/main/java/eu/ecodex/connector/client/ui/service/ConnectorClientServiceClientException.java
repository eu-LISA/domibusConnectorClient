/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.service;

import lombok.NoArgsConstructor;

/**
 * The {@code ConnectorClientServiceClientException} class represents an exception that can be
 * thrown by the {@code ConnectorClientServiceClient} class.
 */
@NoArgsConstructor
public class ConnectorClientServiceClientException extends Throwable {
    private static final long serialVersionUID = 1L;

    public ConnectorClientServiceClientException(String message) {
        super(message);
    }

    public ConnectorClientServiceClientException(Throwable cause) {
        super(cause);
    }

    public ConnectorClientServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorClientServiceClientException(
        String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
