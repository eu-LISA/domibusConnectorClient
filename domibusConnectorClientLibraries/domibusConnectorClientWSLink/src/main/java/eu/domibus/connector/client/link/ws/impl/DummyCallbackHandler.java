package eu.domibus.connector.client.link.ws.impl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

public class DummyCallbackHandler implements CallbackHandler {

	public DummyCallbackHandler() {
	}

	@Override
	public void handle(Callback[] arg0) throws IOException, UnsupportedCallbackException {
	}

}
