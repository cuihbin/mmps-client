package com.zzvc.mmps.launcher.configurator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.zzvc.mmps.multicast.MulticastBase;

public class ConfigReceiver extends MulticastBase {
	private static Logger logger = Logger.getLogger(ConfigReceiver.class);
	
	private static final int MULTICAST_READ_BUFFER_SIZE = 8192;

	private DatagramPacket packet = null;
	
	private ResourceBundle configResource;
	
	private boolean receiverInitialized = false;
	
	public boolean receive() {
		File configFile = new File(getConfigPath());
		
		boolean configFileExists = false;
		try {
			configResource = new PropertyResourceBundle(new FileReader(configFile));
			configFileExists = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		
		if (!initReceiver()) {
			return configFileExists;
		}
		
		try {
			msocket.receive(packet);
		} catch (SocketTimeoutException e) {
			return configFileExists;
		} catch (IOException e) {
			logger.error("Error receiving multcast data", e);
			return configFileExists;
		}
		
		OutputStream os = null;
		try {
			configResource = new PropertyResourceBundle(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

			os = new FileOutputStream(configFile);
			os.write(packet.getData(), 0, packet.getLength());
		} catch (IOException e) {
			logger.info("Error writing multicast data to file.", e);
			receiverInitialized = false;
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
		
		return true;
	}
	
	public ResourceBundle getConfigResource() {
		return configResource;
	}
	
	private boolean initReceiver() {
		if (receiverInitialized) {
			return true;
		}
		
		try {
			if (initMulticast()) {
				msocket.setSoTimeout(getPeriod() * 2);
			}
		} catch (IOException e) {
			logger.error("Error initializing multicast.", e);
			return false;
		}
		
		byte[] buf = new byte[MULTICAST_READ_BUFFER_SIZE];
		packet = new DatagramPacket(buf, buf.length, group, getPort());
		receiverInitialized = true;
		
		return true;
	}
}
