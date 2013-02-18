package com.zzvc.mmps.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import com.zzvc.mmps.configurator.ConfigBase;

public class ConfigSender extends ConfigBase {
	private static Logger logger = Logger.getLogger(ConfigSender.class);
	
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	
	public boolean initSender() {
		try {
			initConfig();
			socket = new DatagramSocket();
		} catch (IOException e) {
			logger.error("Error initializing multicast.", e);
			return false;
		}
		
		InputStream is = null;
		try {
			byte[] buf = getConfigUtil().readConfig();
			packet = new DatagramPacket(buf, buf.length, getGroup(), getPort());
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
		socket.send(packet);
	}
	
}
