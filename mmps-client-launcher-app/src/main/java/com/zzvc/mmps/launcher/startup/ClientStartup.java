package com.zzvc.mmps.launcher.startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ClientStartup {
	private static Logger logger = Logger.getLogger(ClientStartup.class);
	
	public void startup() {
		try {
			Process proc = Runtime.getRuntime().exec("cmd /c startup.bat", null, new File("bin"));
			waitForProcessToTerminate(proc);
		} catch (IOException e) {
			logger.error("Error startup client", e);
			throw new StartupException("Error startup client", e);
		}
	}
	
	private void waitForProcessToTerminate(Process proc) {
		readFromStream(proc.getErrorStream());
		readFromStream(proc.getInputStream());
	}
	
	private void readFromStream(InputStream stream) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					for (String line = reader.readLine(); line != null; line = reader.readLine()) {
						System.out.println(" * " + line);
					}
				} catch (IOException e) {
					logger.error("Failed to read from InputStream", e);
				}
			}
		};
		t.start();
	}
}
