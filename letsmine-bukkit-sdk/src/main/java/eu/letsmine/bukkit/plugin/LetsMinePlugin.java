package eu.letsmine.bukkit.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import eu.letsmine.bukkit.ILetsMineBukkitPlugin;

public class LetsMinePlugin extends JavaPlugin implements ILetsMineBukkitPlugin {
	
	private static LetsMinePlugin instance;
	
	public static LetsMinePlugin getInstance() {
		return instance;
	}
	
	@Override
	public void onLoad() {
		instance = this;
	}

}
