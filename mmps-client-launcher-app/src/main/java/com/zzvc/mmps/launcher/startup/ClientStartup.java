package com.zzvc.mmps.launcher.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class ClientStartup {
	private static Logger logger = Logger.getLogger(ClientStartup.class);
	
	private static final int NUM_OF_PROCESSES = 2;
	private final CountDownLatch endAllSignal = new CountDownLatch(NUM_OF_PROCESSES);
	
	public void startup() throws IOException {
		Process proc = Runtime.getRuntime().exec("cmd /c startup.bat", null, new File("bin"));
		waitForProcessToTerminate(proc);
	}
	
	private void waitForProcessToTerminate(Process proc) {
		ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_PROCESSES);
		executor.execute(createStreamConsumeThread(proc.getErrorStream()));
		executor.execute(createStreamConsumeThread(proc.getInputStream()));
		executor.shutdown();
		try {
			endAllSignal.await();
		} catch (InterruptedException e) {
			logger.warn("Thread sync interrupted", e);
		}
	}
	
	private Runnable createStreamConsumeThread(final InputStream in) {
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					IOUtils.toString(in);
				} catch (IOException e) {
					logger.error("Failed to read from InputStream", e);
				} finally {
					endAllSignal.countDown();
				}
			}
		};
	}
}
