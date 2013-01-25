package com.zzvc.mmps.launcher.configurator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
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
	
	private ResourceBundle configResource;
	
	public boolean receive() {
		initReceiver();
		return doReceive();
	}
	
	public ResourceBundle getConfigResource() {
		return configResource;
	}
	
	private void initReceiver() {
		try {
			if (!initMulticast()) {
				return;
			}
			
			try {
				configResource = new PropertyResourceBundle(new FileReader(getConfigPath()));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
			
			socket = new MulticastSocket(getPort());
			socket.joinGroup(group);
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
					socket.leaveGroup(group);
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		
		File configFile = new File(getConfigPath());
		OutputStream os = null;
		try {
			configResource = new PropertyResourceBundle(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

			os = new FileOutputStream(configFile);
			os.write(packet.getData(), 0, packet.getLength());
			
			return true;
		} catch (IOException e) {
			logger.info("Error writing multicast data to file.", e);
			throw new ConfigException("Error writing multicast data to file.", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
