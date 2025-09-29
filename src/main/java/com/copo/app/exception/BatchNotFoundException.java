package com.copo.app.exception;

public class BatchNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchNotFoundException(String message) {
        super(message);
    }

    public BatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
