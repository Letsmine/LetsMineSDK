package eu.letsmine.bukkit;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import eu.letsmine.bukkit.plugin.LetsMinePlugin;
import eu.letsmine.sdk.ILetsMinePlugin;
import eu.letsmine.sdk.SessionKeys;
import eu.letsmine.session.SessionAPI;
import eu.letsmine.session.SessionObject;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public interface ILetsMineBukkitPlugin extends ILetsMinePlugin<CommandSender>, Plugin {
	
	@Override
	default UUID getSystemUUID() {
		return UUID.fromString("00000000-0000-0000-0000-000000000000");
	}

	@Override
	default UUID getUUID(CommandSender sender) {
		if (sender instanceof Player) {
			return ((Player)sender).getUniqueId();
		} else {
			return getSystemUUID();
		}
	}
	
	default String wrapWorldNameToBukkitName(String originalWorldName, boolean nether, boolean end) {
		String worldName = originalWorldName;
		if (!worldName.endsWith(LetsMinePlugin.netherSuffix) && !worldName.endsWith(LetsMinePlugin.endSuffix) ) {
			if (nether) {
				worldName += LetsMinePlugin.netherSuffix;
			} else if (end) {
				worldName += LetsMinePlugin.endSuffix;
			}
		}
		return worldName;
	}
	
	@Override
	default ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(getName(), locale, getClass().getClassLoader());
	}
	
	@Override
	default ResourceBundle getSDKBundle(Locale locale) {
		return ResourceBundle.getBundle("LetsmineBukkit", locale, LetsMinePlugin.getInstance().getClass().getClassLoader());
	}

	@Override
	default Locale getLocale(UUID uuid) {
		Player player = getServer().getPlayer(uuid);
		if (player == null || uuid.equals(getSystemUUID())) {
			return Locale.getDefault();
		} else {
			return getLocale(player);
		}
	}
	
	@Override
	default Locale getLocale(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player)sender;
			Locale locale = Locale.forLanguageTag(p.getLocale().replace( '_', '-' ));
			if (locale == null) {
				locale = Locale.getDefault();
				try {
					throw new NullPointerException("Player hat no Language: " + sender.getName());
				} catch (NullPointerException e) {
					getLogger().log(Level.SEVERE, e.getMessage(), e);
				}
			}
			return locale;
		} else {
			return Locale.getDefault();
		}
	}

	@Override
	default void sendMessage(ChatMessageType position, UUID uuid, BaseComponent[] message) {
		if (uuid.equals(getSystemUUID())) {
			getServer().getConsoleSender().spigot().sendMessage(message);
		} else {
			Player player = getServer().getPlayer(uuid);
			if (player != null && player.isOnline()) {
				player.spigot().sendMessage(position, message);
			}
		}
	}

	@Override
	default void sendMessage(ChatMessageType position, CommandSender sender, BaseComponent[] message) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			player.spigot().sendMessage(position, message);
		} else {
			sender.spigot().sendMessage(message);
		}
	}

	@Override
	default void sendMessage(ChatMessageType position, UUID uuid, String bundleKey, Object... args) {
		sendMessage(position, uuid, getBundleAsComponent(bundleKey, getLocale(uuid), args));
	}

	@Override
	default void sendMessage(ChatMessageType position, CommandSender sender, String bundleKey, Object... args) {
		sendMessage(position, sender, getBundleAsComponent(bundleKey, getLocale(sender), args));
	}

	@Override
	default void sendMessageToAll(ChatMessageType position, String permission, String bundleKey, Object... args) {
		getServer().getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).forEach(p -> sendMessage(position, p, bundleKey, args));
	}

	@Override
	default void sendDebugMessage(CommandSender sender, String bundleKey, Object... args) {
		UUID uuid = getUUID(sender);
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(uuid, bundleKey, args);
		}
	}

	@Override
	default void sendDebugMessage(ChatMessageType position, CommandSender sender, String bundleKey, Object... args) {
		UUID uuid = getUUID(sender);
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(position, uuid, bundleKey, args);
		}
	}
	
}
