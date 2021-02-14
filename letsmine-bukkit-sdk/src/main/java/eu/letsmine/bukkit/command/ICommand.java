package eu.letsmine.bukkit.command;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import eu.letsmine.bukkit.BundleKeys;
import eu.letsmine.bukkit.ILetsMineBukkitPlugin;
import eu.letsmine.sdk.IMessageCMDProxyCommand;
import net.md_5.bungee.api.ChatMessageType;

public abstract class ICommand<T extends ILetsMineBukkitPlugin> implements IMessageCMDProxyCommand<T, CommandSender>, CommandExecutor {
	
	protected T plugin;
	protected String commandString;
	protected PluginCommand command;
	
	private String className = null;
	
	public ICommand(T plugin, String cmd) {
		this.plugin = plugin;
		this.commandString = cmd;
		command = plugin.getServer().getPluginCommand(commandString);
		if (command == null) {
			return;
		}
		command.setExecutor(this);
		plugin.getLogger().info(plugin.getBundleValue(BundleKeys.ICommandEnabled, plugin.getLocale(plugin.getSystemUUID()), cmd));
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
	
	protected boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	
	protected Player asPlayer(CommandSender sender) {
		if (isPlayer(sender)) {
			return (Player)sender;
		} else {
			return null;
		}
	}
	
	protected boolean sendErrorIfNotPlayer(CommandSender sender) {
		boolean result = false;
		if (!isPlayer(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, BundleKeys.ICommandPlayerOnly);
			result = true;
		}
		return result;
	}
	
	protected boolean sendPlayerNotFound(CommandSender sender) {
		boolean result = false;
		if (!isPlayer(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, BundleKeys.ICommandPlayerNotFound);
			result = true;
		}
		return result;
	}
	
	protected boolean isConsole(CommandSender sender) {
		return sender instanceof ConsoleCommandSender;
	}
	
	protected ConsoleCommandSender asConsole(CommandSender sender) {
		if (isConsole(sender)) {
			return (ConsoleCommandSender)sender;
		} else {
			return null;
		}
	}
	
	protected boolean sendErrorIfNotConsole(CommandSender sender) {
		boolean result = false;
		if (!isConsole(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, BundleKeys.ICommandConsoleOnly);
			result = true;
		}
		return result;
	}
	
	protected boolean isBlock(CommandSender sender) {
		return sender instanceof BlockCommandSender;
	}
	
	protected BlockCommandSender asBlock(CommandSender sender) {
		if (isBlock(sender)) {
			return (BlockCommandSender)sender;
		} else {
			return null;
		}
	}
	
	protected boolean sendErrorIfNotBlock(CommandSender sender) {
		boolean result = false;
		if (!isBlock(sender)) {
			plugin.sendMessage(ChatMessageType.SYSTEM, sender, BundleKeys.ICommandBlockOnly);
			result = true;
		}
		return result;
	}
	
	protected Collection<Entity> getEntityTargetsByArgument(String arg, Location loc) {
		Collection<Entity> results;
		if (arg.startsWith("@")) {
			if (arg.startsWith("@p")) {
				results = loc.getWorld().getEntities().stream().filter(e -> e instanceof Player).sorted((o, t) -> Double.compare(o.getLocation().distance(loc), t.getLocation().distance(loc))).limit(1).collect(Collectors.toList());
			} else if (arg.startsWith("@a")) {
				results = loc.getWorld().getEntities().stream().filter(e -> e instanceof Player).sorted((o, t) -> Double.compare(o.getLocation().distance(loc), t.getLocation().distance(loc))).collect(Collectors.toList());
			} else if (arg.startsWith("@e")) {
				results = loc.getWorld().getEntities().stream().sorted((o, t) -> Double.compare(o.getLocation().distance(loc), t.getLocation().distance(loc))).collect(Collectors.toList());
			} else {
				results = Collections.emptyList();
			}
		} else {
			Player p;
			if (arg.length() == 36) {
				// UUID
				try {
					UUID uuid = UUID.fromString(arg);
					p = plugin.getServer().getPlayer(uuid);
				} catch (Exception e) {
					return Collections.emptyList();
				}
			} else {
				p = plugin.getServer().getPlayer(arg);
			}
			if (p == null) {
				results = Collections.emptyList();
			} else {
				results = Collections.singleton(p);
			}
		}
		return results;
	}

	@Override
	public boolean hasCommandPermission(CommandSender sender, String... args) {
		String argsJoin = String.join(".", args);
		String permissionJoin = String.join(".", command.getPermission(), argsJoin);
		return sender.hasPermission(permissionJoin);
	}
	
	@Override
	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
	
}