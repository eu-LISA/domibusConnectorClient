package eu.domibus.connector.client.exception;

public class DCCMessageValidationException extends DomibusConnectorClientException {

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DCCMessageValidationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DCCMessageValidationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DCCMessageValidationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DCCMessageValidationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public DCCMessageValidationException() {
		// TODO Auto-generated constructor stub
	}

}
