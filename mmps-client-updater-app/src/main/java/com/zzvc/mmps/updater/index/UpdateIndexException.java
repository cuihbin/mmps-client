package com.zzvc.mmps.updater.index;

public class UpdateIndexException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UpdateIndexException() {
		super();
	}

	public UpdateIndexException(String message) {
		super(message);
	}

	public UpdateIndexException(Throwable cause) {
		super(cause);
	}

	public UpdateIndexException(String message, Throwable cause) {
		super(message, cause);
	}

}
