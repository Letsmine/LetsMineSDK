package eu.letsmine.bungee;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

import eu.letsmine.bungee.plugin.LetsMinePlugin;
import eu.letsmine.sdk.ILetsMinePlugin;
import eu.letsmine.sdk.SessionKeys;
import eu.letsmine.session.SessionAPI;
import eu.letsmine.session.SessionObject;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class ILetsMineBungeePlugin extends Plugin implements ILetsMinePlugin<CommandSender> {
	
	@Getter
	private final UUID systemUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Override
	public UUID getUUID(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			return ((ProxiedPlayer)sender).getUniqueId();
		} else {
			return getSystemUUID();
		}
	}

	@Override
	public ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(getDescription().getName(), locale, getClass().getClassLoader());
	}
	
	@Override
	public ResourceBundle getSDKBundle(Locale locale) {
		return ResourceBundle.getBundle("LetsmineBungee", locale, LetsMinePlugin.getInstance().getClass().getClassLoader());
	}
	
	/**
	 * Only for Online Users, returns Locale.getDefault() if Player is Offline
	 * @param uuid
	 * @return
	 */
	@Override
	public Locale getLocale(UUID uuid) {
		ProxiedPlayer player = getProxy().getPlayer(uuid);
		if (player == null || uuid.equals(getSystemUUID())) {
			return Locale.getDefault();
		} else {
			return getLocale(player);
		}
	}

	@Override
	public Locale getLocale(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer)sender;
			Locale locale = p.getLocale();
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
	public void sendMessage(ChatMessageType position, UUID uuid, BaseComponent[] message) {
		if (uuid.equals(getSystemUUID())) {
			getProxy().getConsole().sendMessage(message);
		} else {
			ProxiedPlayer player = getProxy().getPlayer(uuid);
			if (player != null && player.isConnected()) {
				player.sendMessage(message);
			}
		}
	}

	@Override
	public void sendMessage(ChatMessageType position, CommandSender sender, BaseComponent[] message) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer)sender;
			player.sendMessage(position, message);
		} else {
			sender.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(ChatMessageType position, UUID uuid, String bundleKey, Object... args) {
		sendMessage(position, uuid, getBundleAsComponent(bundleKey, getLocale(uuid), args));
	}

	@Override
	public void sendMessage(ChatMessageType position, CommandSender sender, String bundleKey, Object... args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer)sender;
			player.sendMessage(position, getBundleAsComponent(bundleKey, getLocale(sender), args));
		} else {
			sender.sendMessage(getBundleAsComponent(bundleKey, getLocale(sender), args));
		}
	}
	
	@Override
	public void sendMessageToAll(ChatMessageType position, String permission, String bundleKey, Object... args) {
		Stream<ProxiedPlayer> stream = getProxy().getPlayers().stream();
		if (permission != null) {
			stream = stream.filter(p -> p.hasPermission(permission));
		}
		stream.forEach(p -> sendMessage(position, p, bundleKey, args));
	}

	@Override
	public void sendDebugMessage(CommandSender sender, String bundleKey, Object... args) {
		UUID uuid = getUUID(sender);
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(uuid, bundleKey, args);
		}
	}

	@Override
	public void sendDebugMessage(ChatMessageType position, CommandSender sender, String bundleKey, Object... args) {
		UUID uuid = getUUID(sender);
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(position, uuid, bundleKey, args);
		}
	}
	
}
