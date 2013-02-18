package com.zzvc.mmps.launcher.updater;

public class UpdateFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UpdateFileException() {
		super();
	}

	public UpdateFileException(String message) {
		super(message);
	}

	public UpdateFileException(Throwable cause) {
		super(cause);
	}

	public UpdateFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
