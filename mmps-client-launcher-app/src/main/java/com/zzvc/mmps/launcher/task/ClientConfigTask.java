package com.zzvc.mmps.launcher.task;

import org.springframework.beans.factory.annotation.Autowired;

import com.zzvc.mmps.launcher.configurator.ConfigException;
import com.zzvc.mmps.launcher.configurator.ConfigReceiver;
import com.zzvc.mmps.task.TaskSupport;

public class ClientConfigTask extends TaskSupport {
	@Autowired
	private ClientUpdaterTask autoUpdateTask;

	public ClientConfigTask() {
		super();
		pushBundle("ClientLauncherResources");
	}

	@Override
	public void init() {
		infoMessage("client.launcher.configurator.loading");
		
		ConfigReceiver configReceiver = new ConfigReceiver();
		try {
			while (!configReceiver.receive()) {
				infoMessage("client.launcher.configurator.retry");
			}
		} catch (ConfigException e) {
			warnMessage("client.launcher.configurator.warn.receiving");
		}
		
		try {
			autoUpdateTask.setConfigResource(configReceiver.getConfigResource());
			infoMessage("client.launcher.configurator.loadsuccess");
		} catch (ConfigException e) {
			errorMessage("client.launcher.configurator.error.loadfailed");
		}
	}

}
