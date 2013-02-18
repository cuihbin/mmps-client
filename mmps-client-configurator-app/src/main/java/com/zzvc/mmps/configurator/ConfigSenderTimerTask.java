package com.zzvc.mmps.configurator;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import com.zzvc.mmps.console.localize.LocalizeUtil;

public class ConfigSenderTimerTask extends TimerTask {
	private ConfiguratorApp app;
	private ConfigSender configSender;
	
	public ConfigSenderTimerTask(ConfiguratorApp app, ConfigSender configSender) {
		this.app = app;
		this.configSender = configSender;
	}

	public void run() {
		try {
			configSender.send();
			app.statusMessage("client.configurator.sender.sendsuccess", LocalizeUtil.formatDateTimeMedium(new Date()));
		} catch (IOException e) {
			app.statusMessage("client.configurator.sender.sendfailed", LocalizeUtil.formatDateTimeMedium(new Date()));
		}
	}
}
