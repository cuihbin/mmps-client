package com.zzvc.mmps.launcher.configurator;

import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.zzvc.mmps.multicast.MulticastBase;

public class ConfigReceiver extends MulticastBase {
	private static Logger logger = Logger.getLogger(ConfigReceiver.class);
	
	private static final int MULTICAST_READ_BUFFER_SIZE = 8192;

	private MulticastSocket socket;
	private DatagramPacket packet;
	
	public boolean receive() {
		initReceiver();
		return doReceive();
	}
	
	public ResourceBundle getConfigResource() {
		try {
			return new PropertyResourceBundle(new FileReader(getConfigPath()));
		} catch (IOException e) {
			logger.error("Error loading config", e);
			throw new ConfigException("Error loading config", e);
		}
	}
	
	private void initReceiver() {
		try {
			initMulticast();
			socket = new MulticastSocket(getPort());
			socket.joinGroup(getGroup());
			socket.setSoTimeout(getPeriod() * 2);
		} catch (IOException e) {
			logger.error("Error initializing multicast", e);
			throw new ConfigException("Error initializing multicast", e);
		}
		
		byte[] buf = new byte[MULTICAST_READ_BUFFER_SIZE];
		packet = new DatagramPacket(buf, buf.length);
	}
	
	private boolean doReceive() {
		try {
			socket.receive(packet);
		} catch (SocketTimeoutException e) {
			return false;
		} catch (IOException e) {
			logger.error("Error receiving multcast data", e);
			throw new ConfigException("Error receiving multcast data", e);
		} finally {
			if (socket != null) {
				try {
					socket.leaveGroup(getGroup());
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		
		try {
			writeConfig(packet.getData(), packet.getLength());
			return true;
		} catch (IOException e) {
			logger.info("Error writing multicast data to file.", e);
			throw new ConfigException("Error writing multicast data to file.", e);
		}
	}
}
