package eu.letsmine.bungee.command;

import eu.letsmine.bungee.ILetsMineBungeePlugin;
import eu.letsmine.sdk.IMessageCMDProxyCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class ICommand<T extends ILetsMineBungeePlugin> extends Command implements IMessageCMDProxyCommand<T, CommandSender> {

	protected T plugin;
	private String className = null;
	
	public ICommand(T plugin, String cmd, String... aliases) {
		super(cmd, PermissionBuilder(plugin, cmd), aliases);
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerCommand(plugin, this);
		plugin.getLogger().info(plugin.getBundleValue("eu.letsmine.bungee.command.ICommand.enabled", plugin.getLocale(plugin.getSystemUUID()), cmd, String.join(" ", aliases)));
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
	
	private static String PermissionBuilder(ILetsMineBungeePlugin plugin, String cmd) {
		StringBuilder sb = new StringBuilder(plugin.getDescription().getName());
		sb.append(".command.");
		sb.append(cmd);
		return sb.toString();
	}
	
	protected void sendCommandToServer(CommandSender sender, String[] args) {
		if (isPlayer(sender)) {
			asPlayer(sender).chat("/"  + getName() + " " + String.join(" ", args));
		}
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
	
	protected boolean sendErrorIfNotPlayer(CommandSender sender) {
		boolean result = false;
		if (!isPlayer(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, "eu.letsmine.bungee.command.ICommand.playerOnly");
			result = true;
		}
		return result;
	}
	
	protected void sendPlayerNotFound(CommandSender sender) {
		plugin.sendMessage(ChatMessageType.SYSTEM, sender, "eu.letsmine.bungee.command.ICommand.playerNotFound");
	}
	
	protected boolean isConsole(CommandSender sender) {
		return !isPlayer(sender); // Über die API kein zugriff auf ConsoleSender
	}
	
	protected boolean sendErrorIfNotConsole(CommandSender sender) {
		boolean result = false;
		if (!isConsole(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, "eu.letsmine.bungee.command.ICommand.consoleOnly");
			result = true;
		}
		return result;
	}

	@Override
	public boolean hasCommandPermission(CommandSender sender, String... args) {
		String argsJoin = String.join(".", args);
		String permissionJoin = String.join(".", getPermission(), argsJoin);
		return sender.hasPermission(permissionJoin);
	}
	
}
