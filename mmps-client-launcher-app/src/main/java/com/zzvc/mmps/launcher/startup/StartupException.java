package com.zzvc.mmps.launcher.startup;

public class StartupException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public StartupException() {		super();
	}

	public StartupException(String message) {
		super(message);
	}

	public StartupException(Throwable cause) {
		super(cause);
	}

    public StartupException(String message, Throwable cause) {
    	super(message, cause);
    }

}
