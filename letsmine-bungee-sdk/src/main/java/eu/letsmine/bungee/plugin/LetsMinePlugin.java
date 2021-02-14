package eu.letsmine.bungee.plugin;

import eu.letsmine.bungee.ILetsMineBungeePlugin;

public class LetsMinePlugin extends ILetsMineBungeePlugin {
	
	private static LetsMinePlugin instance;
	
	public static LetsMinePlugin getInstance() {
		return instance;
	}
	
	@Override
	public void onLoad() {
		instance = this;
	}

}
