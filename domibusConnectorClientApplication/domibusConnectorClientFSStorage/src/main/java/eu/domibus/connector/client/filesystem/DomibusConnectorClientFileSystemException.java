package eu.domibus.connector.client.filesystem;

import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;

public class DomibusConnectorClientFileSystemException extends DomibusConnectorClientStorageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4383246883598401524L;

	public DomibusConnectorClientFileSystemException() {
	}

	public DomibusConnectorClientFileSystemException(String message) {
		super(message);
	}

	public DomibusConnectorClientFileSystemException(Throwable cause) {
		super(cause);
	}

	public DomibusConnectorClientFileSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomibusConnectorClientFileSystemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
