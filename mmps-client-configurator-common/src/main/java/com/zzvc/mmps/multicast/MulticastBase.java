package com.zzvc.mmps.multicast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

abstract public class MulticastBase {
	private InetAddress group;
	private String configPath;
	
	private String address;
	private int port;
	private int period = 5000;
	
	public InetAddress getGroup() {
		return group;
	}
	
	public String getConfigPath() {
		return configPath;
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

	protected void initMulticast() throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("configurator");
		address = bundle.getString("multicast.address");
		port = Integer.parseInt(bundle.getString("multicast.port"));
		period = Integer.parseInt(bundle.getString("config.period"));
		
		group = InetAddress.getByName(address);
		configPath = bundle.getString("config.path");
	}
	
	protected byte[] readConfig() throws IOException {
		InputStream is = new FileInputStream(configPath);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(os);
		try {
			IOUtils.copy(is, zos);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(zos);
		}
		return os.toByteArray();
	}
	
	protected void writeConfig(byte[] buf, int length) throws IOException {
		OutputStream os = new FileOutputStream(configPath);
		try {
			IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(buf, 0, length)), os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
