/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.link.ws;

import lombok.NoArgsConstructor;

/**
 * WsPolicyLoaderException is a subclass of RuntimeException that represents exceptions encountered
 * during the loading of a WS policy.
 *
 * <p>This exception can occur in the WsPolicyLoader class while loading a policy from a given
 * resource. It can be thrown if the WS policy cannot be read or cannot be parsed.
 */
@NoArgsConstructor
public class WsPolicyLoaderException extends RuntimeException {
    public WsPolicyLoaderException(String message) {
        super(message);
    }

    public WsPolicyLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public WsPolicyLoaderException(Throwable cause) {
        super(cause);
    }

    public WsPolicyLoaderException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
