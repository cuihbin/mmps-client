package com.zzvc.mmps.configurator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

public class ConfigUtil {
	private File configFile;
	
	public ConfigUtil(String configPath) {
		this.configFile = new File(configPath);
	}

	public byte[] readConfig() throws IOException {
		InputStream is = new FileInputStream(configFile);
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
	
	public void writeConfig(byte[] buf, int length) throws IOException {
		OutputStream os = new FileOutputStream(configFile);
		try {
			IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(buf, 0, length)), os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	public File getConfigFile() {
		return configFile;
	}
}
