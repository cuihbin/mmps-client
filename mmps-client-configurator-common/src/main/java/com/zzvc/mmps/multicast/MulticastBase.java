package com.zzvc.mmps.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ResourceBundle;

abstract public class MulticastBase {
	protected MulticastSocket msocket = null;
	protected InetAddress group = null;
	
	protected static ResourceBundle bundle = null;
	
	private String address;
	private int port;
	private int period = 5000;
	private String configPath;
	
	private boolean initMulticastConfig = false;
	
	public MulticastBase() {
		bundle = ResourceBundle.getBundle("configurator");
		address = bundle.getString("multicast.address");
		port = Integer.parseInt(bundle.getString("multicast.port"));
		configPath = bundle.getString("config.path");
		initMulticastConfig = true;
		
		String configPeriod = bundle.getString("config.period");
		period = Integer.parseInt(configPeriod);
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

	protected boolean initMulticast() throws IOException {
		if (!initMulticastConfig) {
			return false;
		}
		
		msocket = new MulticastSocket(port);
		group = InetAddress.getByName(address);
		msocket.joinGroup(group);
		
		return true;
	}

}
