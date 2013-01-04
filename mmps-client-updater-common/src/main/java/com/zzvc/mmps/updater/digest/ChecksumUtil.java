package com.zzvc.mmps.updater.digest;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

public class ChecksumUtil {
	private static Map<String, FileDigest> fileDigestMap = new HashMap<String, FileDigest>();
	
	public static String getChecksum(File file, String checksumType) throws IOException, NoSuchAlgorithmException  {
		return new String(Hex.encodeHex(getFileDigest(checksumType).digest(file)));
	}
	
	public static boolean verifyChecksum(File file, String checksumType, String checksum) throws IOException, NoSuchAlgorithmException {
		return checksum.equals(getChecksum(file, checksumType));
	}
	
	private static FileDigest getFileDigest(String checksumType) throws NoSuchAlgorithmException {
		if (!fileDigestMap.containsKey(checksumType)) {
			fileDigestMap.put(checksumType, new FileDigest(checksumType));
		}
		return fileDigestMap.get(checksumType);
	}

}
