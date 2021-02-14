package eu.letsmine.bungee.listener;

import java.util.logging.Level;

import eu.letsmine.bungee.ILetsMineBungeePlugin;
import eu.letsmine.sdk.IMessageCMDProxy;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

public class IBungeeListener<T extends ILetsMineBungeePlugin> implements IMessageCMDProxy<T, CommandSender>, Listener {
	
	protected final T plugin;
	private String className = null;
	
	public IBungeeListener(T plugin) {
		this.plugin = plugin;
		try {
			plugin.getProxy().getPluginManager().registerListener(plugin, this);
			plugin.getLogger().info(plugin.getBundleValue("eu.letsmine.bungee.listener.IBungeeListener.enabled", plugin.getLocale(plugin.getSystemUUID()), getClass().getSimpleName()));
		} catch (Exception e) {
			plugin.getLogger().severe(plugin.getBundleValue("eu.letsmine.bungee.listener.IBungeeListener.failed", plugin.getLocale(plugin.getSystemUUID()), getClass().getSimpleName()));
			plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public String getClassName() {
		if (className == null) {
			className = getClass().getName();
		}
		return className;
	}

	@Override
	public T getMessageProxy() {
		return plugin;
	}
	
	protected boolean isPlayer(CommandSender sender) {
		return sender instanceof ProxiedPlayer;
	}
	
	protected ProxiedPlayer asPlayer(CommandSender sender) {
		if (isPlayer(sender)) {
			return (ProxiedPlayer)sender;
		} else {
			return null;
		}
	}
	
	protected boolean isConsole(CommandSender sender) {
		return !isPlayer(sender); // Über die API kein zugriff auf ConsoleSender
	}
	
}