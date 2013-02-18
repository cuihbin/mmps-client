package com.zzvc.mmps.configurator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ResourceBundle;

abstract public class ConfigBase {
	private InetAddress group;
	private ConfigUtil configUtil;
	
	private String address;
	private int port;
	private int period = 5000;
	
	public InetAddress getGroup() {
		return group;
	}
	
	public ConfigUtil getConfigUtil() {
		return configUtil;
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

	protected void initConfig() throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("configurator");
		address = bundle.getString("multicast.address");
		port = Integer.parseInt(bundle.getString("multicast.port"));
		period = Integer.parseInt(bundle.getString("config.period"));
		
		group = InetAddress.getByName(address);
		configUtil = new ConfigUtil(bundle.getString("config.path"));
	}
}
