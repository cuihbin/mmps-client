package com.zzvc.mmps.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ResourceBundle;

abstract public class MulticastBase {
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
		period = Integer.parseInt(bundle.getString("config.period"));

		initMulticastConfig = true;
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
		
		group = InetAddress.getByName(address);
		return true;
	}

}
