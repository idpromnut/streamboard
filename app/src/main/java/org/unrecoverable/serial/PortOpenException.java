package org.unrecoverable.serial;

public class PortOpenException extends RuntimeException {

	public PortOpenException(String iMessage) {
		super(iMessage);
	}

	public PortOpenException(String iMessage, Throwable iCause) {
		super(iMessage, iCause);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2478180284662332084L;

}
