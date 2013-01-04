package com.zzvc.mmps.updater;

import java.util.Map;

import com.zzvc.mmps.app.AppArgsListener;
import com.zzvc.mmps.app.util.BeanFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BeanFactory.start();
		
    	Map<String, AppArgsListener> listeners = BeanFactory.getBeansOfType(AppArgsListener.class);
    	for (AppArgsListener listener : listeners.values()) {
    		listener.processArgs(args);
    	}
	}

}
