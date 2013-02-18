package com.zzvc.mmps.updater.digest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileDigest {
	private String type;
	
	private MessageDigest md;

	public FileDigest(String type) throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance(type);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public byte[] digest(File file) throws IOException {
		DigestInputStream dis = null;
		try {
			dis = new DigestInputStream(new FileInputStream(file), md);
			byte[] readBuffer = new byte[8192];
			for (;dis.read(readBuffer) != -1;);
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
		}
		
		return md.digest();
	}
	
}
