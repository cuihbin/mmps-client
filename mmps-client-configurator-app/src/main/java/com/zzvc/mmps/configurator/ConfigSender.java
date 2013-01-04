package com.zzvc.mmps.configurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.zzvc.mmps.multicast.MulticastBase;

public class ConfigSender extends MulticastBase {
	private static Logger logger = Logger.getLogger(ConfigSender.class);
	
	private DatagramPacket packet = null;
	
	public boolean initSender() {
		if (!initMulticast()) {
			return false;
		}
		
		try {
			msocket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
		} catch (SocketException e) {
			logger.error("Error setting local network interface.", e);
			return false;
		} catch (UnknownHostException e) {
			logger.error("Error setting local network interface.", e);
			return false;
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(getConfigPath());
			byte[] buf = new byte[8192];
			int bufLen = is.read(buf);
			packet = new DatagramPacket(buf, bufLen, group, getPort());
		} catch (FileNotFoundException e) {
			logger.error("Configurator data file not found.", e);
			return false;
		} catch (IOException e) {
			logger.error("Configurator data reading error.", e);
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		
		return true;
	}
	
	public void send() throws IOException {
		msocket.send(packet);
	}
	
}
