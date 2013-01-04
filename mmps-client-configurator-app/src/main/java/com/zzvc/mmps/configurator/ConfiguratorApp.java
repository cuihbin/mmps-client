package com.zzvc.mmps.configurator;

import java.util.Timer;

import com.zzvc.mmps.app.AppSupport;

public class ConfiguratorApp extends AppSupport {
	
	private ConfigSender configSender;
	private Timer timer;
	
	public ConfiguratorApp() {
		super();
		pushBundle("ConfiguratorResources");
	}

	@Override
	public void init() {
		configSender = new ConfigSender();
		if (configSender.initSender()) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new ConfigSenderTimerTask(this, configSender), configSender.getPeriod(), configSender.getPeriod());

			infoMessage("client.configurator.sender.loadsuccess");
			infoMessage("client.configurator.sender.parameter", configSender.getAddress(), configSender.getPort(), configSender.getPeriod());
		} else {
			errorMessage("client.configurator.sender.loadfailed");
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
