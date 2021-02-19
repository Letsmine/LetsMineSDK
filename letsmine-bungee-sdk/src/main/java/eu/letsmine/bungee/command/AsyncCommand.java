package eu.letsmine.bungee.command;

import java.util.logging.Level;

import eu.letsmine.bungee.ILetsMineBungeePlugin;
import eu.letsmine.sdk.SessionKeys;
import eu.letsmine.session.SessionAPI;
import eu.letsmine.session.SessionObject;
import net.md_5.bungee.api.CommandSender;

public abstract class AsyncCommand<T extends ILetsMineBungeePlugin> extends ICommand<T> {

	public AsyncCommand(T plugin, String cmd, String... aliases) {
		super(plugin, cmd, aliases);
		this.plugin = plugin;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, () -> runExecuteAsync(sender, args));
	}
	
	private void runExecuteAsync(CommandSender sender, String[] args) {
		SessionObject so = SessionAPI.getSessionObject(plugin.getUUID(sender));
		long startTime = System.currentTimeMillis();
		try {
			if (so.contains(SessionKeys.Debug)) {
				plugin.sendMessage(sender, "eu.letsmine.bungee.command.AsyncCommand.start", getName());
			}
			executeAsync(sender, args);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
			plugin.sendMessage(sender, "eu.letsmine.bungee.command.AsyncCommand.error");
		} finally {
			if (so.contains(SessionKeys.Debug)) {
				long timeElapse = System.currentTimeMillis() - startTime;
				plugin.sendMessage(sender, "eu.letsmine.bungee.command.AsyncCommand.stop", getName(), timeElapse);
			}
		}
	}
	
	public abstract void executeAsync(CommandSender sender,String[] args);
	
}
