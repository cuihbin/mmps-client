package com.zzvc.mmps.launcher.task;

import org.springframework.beans.factory.annotation.Autowired;

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
		while (!configReceiver.receive()) {
			infoMessage("client.launcher.configurator.retry");
		}
		
		autoUpdateTask.setConfigResource(configReceiver.getConfigResource());
		infoMessage("client.launcher.configurator.loadsuccess");
	}

}
