package org.unrecoverable.serial;

public class FileLoggingException extends RuntimeException {

	public FileLoggingException(String iMessage) {
		super(iMessage);
	}

	public FileLoggingException(String iMessage, Throwable iCause) {
		super(iMessage, iCause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
