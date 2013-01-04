package com.zzvc.mmps.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

abstract public class MulticastBase {
	private static Logger logger = Logger.getLogger(MulticastBase.class);
	
	protected MulticastSocket msocket = null;
	protected InetAddress group = null;
	
	protected static ResourceBundle bundle = null;
	
	private String address;
	private int port;
	private int period = 5000;
	private String configPath;
	
	private boolean initMulticastConfig = false;
	
	public MulticastBase() {
		try {
			bundle = ResourceBundle.getBundle("configurator");
			address = bundle.getString("multicast.address");
			port = Integer.parseInt(bundle.getString("multicast.port"));
			configPath = bundle.getString("config.path");
			initMulticastConfig = true;
		} catch (MissingResourceException e) {
			logger.error("Multicast config file not found or missing configuration key.", e);
		} catch (NumberFormatException e) {
			logger.error("Invalid multicast port for " + port, e);
		}
		
		String configPeriod = null;
		try {
			configPeriod = bundle.getString("config.period");
			period = Integer.parseInt(configPeriod);
		} catch (NumberFormatException e) {
			logger.warn("Invalid configurator sending period " + configPeriod, e);
		}
	}

	public int getPeriod() {
		return period;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getConfigPath() {
		return configPath;
	}

	protected boolean initMulticast() {
		if (!initMulticastConfig) {
			return false;
		}
		
		try {
			msocket = new MulticastSocket(port);
			group = InetAddress.getByName(address);
			msocket.joinGroup(group);
		} catch (IOException e) {
			logger.error("Cannot init multicast socket.");
			return false;
		}
		
		return true;
	}

}
