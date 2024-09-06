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

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The DefaultWsCallbackHandler class is an implementation of the CallbackHandler interface. It
 * provides a default implementation for handling callbacks in web service clients.
 *
 * <p>When invoked, the DefaultWsCallbackHandler logs a trace message with the callbacks that were
 * received.
 * It does not perform any further action and is intended to be used as a placeholder or default
 * callback handler.
 *
 * <p>This class is typically used in conjunction with the JAX-WS framework to configure and create
 * web service clients.
 * It can be provided as the callback handler when setting up a JAX-WS endpoint or client, and will
 * be called with the appropriate callbacks during the web service invocation process.
 *
 * <p>It is important to note that the DefaultWsCallbackHandler class does not provide any
 * functionality
 * beyond logging the received callbacks. If custom callback handling logic is required, a different
 * implementation of the CallbackHandler interface should be used instead.
 *
 * @see CallbackHandler
 * @see org.apache.cxf.jaxws.JaxWsProxyFactoryBean
 */
@Component("defaultCallbackHandler")
public class DefaultWsCallbackHandler implements CallbackHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWsCallbackHandler.class);

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        LOGGER.trace("default callback handler called with callbacks [{}]", (Object[]) callbacks);
        // just do nothing...
    }
}
