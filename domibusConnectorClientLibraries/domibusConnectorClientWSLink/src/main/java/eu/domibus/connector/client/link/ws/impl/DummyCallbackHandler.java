/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.link.ws.impl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import lombok.NoArgsConstructor;

/**
 * A dummy implementation of the {@link CallbackHandler} interface.
 *
 * <p>This class provides an empty implementation of the {@link #handle(Callback[])} method from
 * the {@link CallbackHandler} interface. It is intended to be used as a placeholder or a base class
 * for implementing callback handlers in the application.
 */
@NoArgsConstructor
public class DummyCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] arg0) {
    }
}
