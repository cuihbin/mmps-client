package com.zzvc.mmps.launcher.task;

import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.zzvc.mmps.launcher.updater.ClientUpdater;
import com.zzvc.mmps.launcher.updater.UpdateFileException;
import com.zzvc.mmps.launcher.updater.UpdateServerConnectionException;
import com.zzvc.mmps.task.TaskException;
import com.zzvc.mmps.task.TaskSupport;

public class ClientUpdaterTask extends TaskSupport {
	private ResourceBundle configResource;
	
	@Autowired
	private ClientStartupTask clientStartupTask;

	public ClientUpdaterTask() {
		super();
		pushBundle("ClientLauncherResources");
	}

	public void setConfigResource(ResourceBundle autoUpdatePath) {
		this.configResource = autoUpdatePath;
	}

	@Override
	public boolean isWaitingPrequisiteInit() {
		return configResource == null;
	}

	@Override
	public void init() {
		infoMessage("client.launcher.updater.updating");
		
		try {
			ClientUpdater clientUpdater = new ClientUpdater(configResource);
			if (!clientUpdater.verifyUpdate()) {
				clientUpdater.update();
			} else {
				infoMessage("client.launcher.updater.uptodate");
			}
		} catch (UpdateServerConnectionException e) {
			warnMessage("client.launcher.updater.warn.connectingserver");
		} catch (UpdateFileException e) {
			errorMessage("client.launcher.updater.error.downloadingfile");
			throw new TaskException("Cannot update file", e);
		}
		
		clientStartupTask.setClientUpdateSuccess(true);
		infoMessage("client.launcher.updater.updatesuccess");
	}
	
}
