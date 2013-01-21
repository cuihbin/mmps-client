package com.zzvc.mmps.launcher.task;

import java.io.IOException;

import sun.misc.Signal;

import com.zzvc.mmps.launcher.startup.ClientStartup;
import com.zzvc.mmps.task.TaskSupport;

public class ClientStartupTask extends TaskSupport {
	private boolean clientUpdateSuccess = false;

	public ClientStartupTask() {
		super();
		pushBundle("ClientLauncherResources");
	}

	public void setClientUpdateSuccess(boolean clientUpdateSuccess) {
		this.clientUpdateSuccess = clientUpdateSuccess;
	}

	@Override
	public boolean isWaitingPrequisiteInit() {
		return !clientUpdateSuccess;
	}

	@Override
	public void init() {
		infoMessage("client.launcher.startup.starting");
		
		ClientStartup clientStartup = new ClientStartup();
		try {
			clientStartup.startup();
			infoMessage("client.launcher.startup.startsuccess");
			Signal.raise(new Signal("TERM"));
		} catch (IOException e) {
			errorMessage("client.launcher.startup.startfailed");
		}
	}

}
