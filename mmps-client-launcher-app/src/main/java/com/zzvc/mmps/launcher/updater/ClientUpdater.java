package com.zzvc.mmps.launcher.updater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import com.zzvc.mmps.documents.UpdateDocument;
import com.zzvc.mmps.documents.UpdateDocument.Update;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir.File;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir.File.Checksum;
import com.zzvc.mmps.updater.digest.ChecksumUtil;
import com.zzvc.mmps.updater.index.IndexDocumentUtil;
import com.zzvc.mmps.updater.index.IndexTimestampUtil;

public class ClientUpdater {
	private static Logger logger = Logger.getLogger(ClientUpdater.class);

	private static final String UPDATE_REVISION_CACHE_FILE = "update.lastUpdate";
	
	private static final int UPDATE_FILE_DOWNLOAD_RETRY = 3;
	
	private String updateUrl;
	
	private Update updateDoc;

	public ClientUpdater(ResourceBundle configResource) {
		this.updateUrl = loadUpdateUrl(configResource);
	}
	
	public boolean verifyUpdate() {
		updateDoc = parseUpdateIndexFromUrl(getUpdateIndexUrl()).getUpdate();
		return checkLastUpdate(updateDoc.getRevision());
	}

	public void update() {
		Dir[] dirDocArray = updateDoc.getDirArray();
		for (Dir dirDoc : dirDocArray) {
			update(dirDoc);
		}
		saveLastUpdate(updateDoc.getRevision());
	}
	
	private String loadUpdateUrl(ResourceBundle configResource) {
		String updateUrl = null;
		try {
			updateUrl = configResource.getString("client.update.url") + "/";
			return new URI(updateUrl).normalize().toString();
		} catch (MissingResourceException e) {
			throw new UpdateServerConnectionException("Update url configure not found", e);
		} catch (URISyntaxException e) {
			throw new UpdateServerConnectionException("Cannot parse update url " + updateUrl, e);
		}
	}

	private UpdateDocument parseUpdateIndexFromUrl(String url) {
		try {
			return IndexDocumentUtil.read(getInputStreamFromUrl(url));
		} catch (XmlException e) {
			throw new UpdateServerConnectionException("Error parsing update index", e);
		} catch (IOException e) {
			throw new UpdateServerConnectionException("Error reading update index from url " + url, e);
		}
	}

	private boolean checkLastUpdate(String revision) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(UPDATE_REVISION_CACHE_FILE));
			String line = reader.readLine();
			String[] splitLine = line.split("=");
			String lastUrl = splitLine[0];
			String lastRevision = splitLine[1];
			return lastUrl.equals(updateUrl) && lastRevision.equals(revision);
		} catch (Exception e) {
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void saveLastUpdate(String revision) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(UPDATE_REVISION_CACHE_FILE));
			writer.write(updateUrl + "=" + revision);
		} catch (Exception e) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private java.io.File update(Dir dirDoc) {
		if (dirDoc.getName().contains(".")) {
			logger.warn("Only subfolders are allowed. Paths with '.' or '..' will be ignored.");
			return null;
		}
		
		java.io.File dir = new java.io.File(dirDoc.getName());
		if (dir.isAbsolute()) {
			logger.warn("Only relative paths are allowed. Absolute paths will be ignored.");
			return null;
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Set<java.io.File> existingFiles = new HashSet<java.io.File>(Arrays.asList(dir.listFiles()));

		for (File fileDoc : dirDoc.getFileArray()) {
			existingFiles.remove(update(dirDoc, fileDoc));
		}
		
		for (java.io.File file : existingFiles) {
			file.delete();
		}
		
		return dir;
	}
	
	private java.io.File update(Dir dirDoc, File fileDoc) {
		String remoteFileUrl = getUpdateFileUrl(dirDoc, fileDoc);
		java.io.File localFile = getLocalFile(dirDoc, fileDoc);
		
		if (verifyFile(fileDoc, localFile)) {
			return localFile;
		}
		
		int retryCount = UPDATE_FILE_DOWNLOAD_RETRY;
		while ((retryCount--) > 0) {
			if (downloadFileFromUrl(remoteFileUrl, localFile) && verifyFile(fileDoc, localFile)) {
				return localFile;
			}
		}
		
		throw new UpdateFileException("Cannot update file from " + remoteFileUrl);
	}
	
	private boolean verifyFile(File fileDoc, java.io.File file) {
		return verifyExists(file) 
				&& verifyTimestamp(fileDoc, file)
				&& verifySize(fileDoc, file)
				&& verifyChecksum(fileDoc.getChecksum(), file);
	}
	
	private boolean verifyExists(java.io.File file) {
		return file.exists();
	}
	
	private boolean verifyTimestamp(File fileDoc, java.io.File file) {
		if (!IndexTimestampUtil.checkTimestamp(fileDoc.getTimestamp(), file.lastModified())) {
			logger.warn("Timestamp not match for file " + file.getName());
		}
		return true;
	}
	
	private boolean verifySize(File fileDoc, java.io.File file) {
		return file.length() == Long.parseLong(fileDoc.getSize());
	}

	private boolean verifyChecksum(Checksum checksumDoc, java.io.File file) {
		try {
			return ChecksumUtil.verifyChecksum(file, checksumDoc.getType(), checksumDoc.getStringValue());
		} catch (NoSuchAlgorithmException e) {
			logger.warn("Unknown checksum type '" + checksumDoc.getType() + "', checksum verify skipped");
			return true;
		} catch (IOException e) {
			logger.error("Error digesting local file", e);
			return false;
		}
	}
	
	private InputStream getInputStreamFromUrl(String url) throws IOException {
		return VFS.getManager().resolveFile(url).getContent().getInputStream();
	}
	
	private String getUpdateIndexUrl() {
		ResourceBundle updaterResource = ResourceBundle.getBundle("updater");
		return updateUrl + updaterResource.getString("client.updater.index");
	}
	
	private String getUpdateFileUrl(Dir dirDoc, File fileDoc) {
		return updateUrl + dirDoc.getName() + "/" + fileDoc.getName();
	}
	
	private java.io.File getLocalFile(Dir dirDoc, File fileDoc) {
		return new java.io.File(new java.io.File(dirDoc.getName()), fileDoc.getName());
	}
	
	private boolean downloadFileFromUrl(String url, java.io.File file) {
		FileContent content = null;
		java.io.File tempFile = null;
		OutputStream os = null;
		try {
			content = VFS.getManager().resolveFile(url).getContent();
			tempFile = java.io.File.createTempFile("temp-", ".tmp");
			os = new FileOutputStream(tempFile);
			IOUtils.copy(content.getInputStream(), os);
		} catch (FileNotFoundException e) {
			logger.error("Error accessing local file", e);
			return false;
		} catch (IOException e) {
			logger.error("Error downloading file from " + url, e);
			return false;
		} finally {
			IOUtils.closeQuietly(os);
		}
		
		if (file.exists() && !file.delete() || !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
			return false;
		}
		
		try {
			return tempFile.renameTo(file) && file.setLastModified(content.getLastModifiedTime());
		} catch (FileSystemException e) {
			logger.error("Error setting file timestamp", e);
			return false;
		}
	}
}
