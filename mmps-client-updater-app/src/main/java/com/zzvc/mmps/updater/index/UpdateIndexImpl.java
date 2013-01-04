package com.zzvc.mmps.updater.index;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.zzvc.mmps.console.ConsoleMessageSupport;
import com.zzvc.mmps.documents.UpdateDocument;
import com.zzvc.mmps.documents.UpdateDocument.Update;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir.File;
import com.zzvc.mmps.documents.UpdateDocument.Update.Dir.File.Checksum;
import com.zzvc.mmps.updater.digest.ChecksumUtil;

public class UpdateIndexImpl extends ConsoleMessageSupport implements UpdateIndex {
	private static Logger logger = Logger.getLogger(UpdateIndexImpl.class);
	
	private String updateDir;
	private String checksumType;
	
	private java.io.File baseDir;
	
	public UpdateIndexImpl() {
		super();
		pushBundle("ClientUpdaterResources");
	}
	
	@Override
	public void create() {
		infoMessage("client.updater.index.starting");
		
		ResourceBundle bundle = ResourceBundle.getBundle("updater");
		updateDir = bundle.getString("client.updater.updatedir");
		checksumType = bundle.getString("client.updater.checksumtype");
		
		baseDir = new java.io.File(updateDir);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			errorMessage("client.updater.index.error.nodir", updateDir);
			return;
		}
		
		UpdateDocument rootDoc = UpdateDocument.Factory.newInstance();
		Update updateDoc = rootDoc.addNewUpdate();
		
		for (java.io.File dir : baseDir.listFiles()) {
			if (dir.isFile()) {
				logger.info("File " + dir.getName() + " ignored as only files in subdir would be indexed");
				continue;
			}
			
			indexDir(updateDoc.addNewDir(), dir);
		}
		updateDoc.setRevision(IndexTimestampUtil.createTimestamp(System.currentTimeMillis()));
		
		saveIndexFile(rootDoc);
		
		infoMessage("client.updater.index.createsuccess");
	}

	@Override
	protected String getConsolePrefix() {
		return "Update Index";
	}
	
	private void indexDir(Dir dirDoc, java.io.File dir) {
		dirDoc.setName(dir.getName());
		for (java.io.File file : dir.listFiles()) {
			indexFile(dirDoc.addNewFile(), file);
		}
	}
	
	private void indexFile(File fileDoc, java.io.File file) {
		fileDoc.setName(file.getName());
		fileDoc.setSize(String.valueOf(file.length()));
		fileDoc.setTimestamp(IndexTimestampUtil.createTimestamp(file.lastModified()));
		checksumFile(fileDoc.addNewChecksum(), file);
	}
	
	private void checksumFile(Checksum checksumDoc, java.io.File file) {
		checksumDoc.setType(checksumType);
		try {
			checksumDoc.setStringValue(ChecksumUtil.getChecksum(file, checksumType));
		} catch (NoSuchAlgorithmException e) {
			throw new UpdateIndexException("Unknown checksum type " + checksumType, e);
		} catch (IOException e) {
			logger.error("Error calculating file digest", e);
			throw new UpdateIndexException("Error calculating file digest", e);
		}
	}
	
	private void saveIndexFile(UpdateDocument updateDocument) {
		ResourceBundle updaterResource = ResourceBundle.getBundle("updater");
		String indexFile = updaterResource.getString("client.updater.index");
		try {
			IndexDocumentUtil.write(updateDocument, new FileOutputStream(new java.io.File(baseDir, indexFile)));
		} catch (IOException e) {
			logger.error("Error saving index file", e);
			throw new UpdateIndexException("Error saving index file", e);
		}
	}

}
