package eu.letsmine.bukkit.command;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import eu.letsmine.bukkit.BundleKeys;
import eu.letsmine.bukkit.ILetsMineBukkitPlugin;
import eu.letsmine.sdk.SessionKeys;
import eu.letsmine.session.SessionAPI;
import eu.letsmine.session.SessionObject;

public abstract class AsyncCommand<T extends ILetsMineBukkitPlugin> extends ICommand<T> {

	public AsyncCommand(T plugin, String cmd) {
		super(plugin, cmd);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> runAsyncCommand(sender, cmd, label, args));
		return true;
	}
	
	private void runAsyncCommand(CommandSender sender, Command cmd, String label, String[] args) {
		SessionObject so = SessionAPI.getSessionObject(plugin.getUUID(sender));
		long startTime = System.currentTimeMillis();
		try {
			if (so.contains(SessionKeys.Debug)) {
				plugin.sendMessage(sender, BundleKeys.AsyncCommandStart, cmd.getLabel());
			}
			onAsyncCommand(sender, cmd, label, args);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
			plugin.sendMessage(sender, BundleKeys.AsyncCommandError);
		} finally {
			if (so.contains(SessionKeys.Debug)) {
				long timeElapse = System.currentTimeMillis() - startTime;
				plugin.sendMessage(sender, BundleKeys.AsyncCommandStop, cmd.getLabel(), timeElapse);
			}
		}
	}
	
	public abstract void onAsyncCommand(CommandSender sender, Command cmd, String label, String[] args);

}
