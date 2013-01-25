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

	protected void initMulticast() throws IOException {
		bundle = ResourceBundle.getBundle("configurator");
		address = bundle.getString("multicast.address");
		port = Integer.parseInt(bundle.getString("multicast.port"));
		configPath = bundle.getString("config.path");
		period = Integer.parseInt(bundle.getString("config.period"));
		
		group = InetAddress.getByName(address);
	}

}
