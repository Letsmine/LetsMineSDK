package eu.letsmine.bukkit.listener;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import eu.letsmine.bukkit.BundleKeys;
import eu.letsmine.bukkit.ILetsMineBukkitPlugin;
import eu.letsmine.sdk.IMessageCMDProxy;

public class IBukkitListener<T extends ILetsMineBukkitPlugin> implements IMessageCMDProxy<T, CommandSender>, Listener {
	
	protected final T plugin;
	
	private String className = null;
	
	public IBukkitListener(T plugin) {
		this.plugin = plugin;
		try {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			plugin.getLogger().info(plugin.getBundleValue(BundleKeys.IBukkitListenerEnabled, plugin.getLocale(plugin.getSystemUUID()), getClass().getSimpleName()));
		} catch (Exception e) {
			plugin.getLogger().severe(plugin.getBundleValue(BundleKeys.IBukkitListenerFailed, plugin.getLocale(plugin.getSystemUUID()), getClass().getSimpleName()));
			plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		
	}

	@Override
	public T getMessageProxy() {
		return plugin;
	}
	
	@Override
	public String getClassName() {
		if (className == null) {
			className = getClass().getName();
		}
		return className;
	}
	
}