package eu.domibus.connector.client.exception;

public class DomibusConnectorClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 260299312166946237L;

	public DomibusConnectorClientException() {
	}

	public DomibusConnectorClientException(String arg0) {
		super(arg0);
	}

	public DomibusConnectorClientException(Throwable arg0) {
		super(arg0);
	}

	public DomibusConnectorClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DomibusConnectorClientException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
