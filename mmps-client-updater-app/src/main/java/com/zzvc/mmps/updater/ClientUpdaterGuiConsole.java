package com.zzvc.mmps.updater;

import javax.annotation.Resource;

import com.zzvc.mmps.gui.console.GuiConsole;
import com.zzvc.mmps.gui.console.GuiConsoleButton;
import com.zzvc.mmps.updater.index.UpdateIndex;

public class ClientUpdaterGuiConsole extends GuiConsole {
	@Resource
	private UpdateIndex updateIndex;

	public ClientUpdaterGuiConsole() {
		super();
		pushBundle("ClientUpdaterResources");
		
		addButton(new GuiConsoleButton(findText("client.updater.gui.button.index")) {

			@Override
			public void buttonAction() {
				if (dialog.showConfirm("client.updater.index.confirm")) {
					updateIndex.create();
				}
			}
			
		});
	}

}
