package com.zzvc.mmps.launcher.updater;

public class UpdateServerConnectionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UpdateServerConnectionException() {
		super();
	}

	public UpdateServerConnectionException(String message) {
		super(message);
	}

	public UpdateServerConnectionException(Throwable cause) {
		super(cause);
	}

	public UpdateServerConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

}
