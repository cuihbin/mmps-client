package com.zzvc.mmps.updater;

import javax.annotation.Resource;

import com.zzvc.mmps.app.AppArgsListener;
import com.zzvc.mmps.updater.index.UpdateIndex;

public class ClientUpdaterArgsListener implements AppArgsListener {
	@Resource
	private UpdateIndex updateIndex;

	@Override
	public void processArgs(String[] args) {
		if (args.length >= 1 && "runIndex".equals(args[0])) {
			updateIndex.create();
		}
	}

}
