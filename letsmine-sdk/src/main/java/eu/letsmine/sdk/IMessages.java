package eu.letsmine.sdk;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import eu.letsmine.session.SessionAPI;
import eu.letsmine.session.SessionObject;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public interface IMessages {
	
	// Bundle Value
	
	/**
	 * Gets a string for the given key from resource bundle
	 * Use the given locale for locale discover
	 * @param bundleKey
	 * @param locale
	 * @return
	 */
	default String getBundleValue(String bundleKey, Locale locale) {
		return getBundleValue(bundleKey, locale, new Object[0]);
	}
	
	/**
	 * Gets a string for the given key from resource bundle
	 * Use the given locale for locale discover
	 * Use String.format on the returned string
	 * @param bundleKey
	 * @param locale
	 * @param args
	 * @return
	 */
	String getBundleValue(String bundleKey, Locale locale, Object... args);
	
	// Bundle Value as BaseCompnent
	
	/**
	 * Gets a string for the given key from resource bundle and try to transform it to a BaseComponent[] with ComponentSerializer.parse(String)
	 * Use the given locale for locale discover
	 * @param bundleKey
	 * @param locale
	 * @return
	 */
	default BaseComponent[] getBundleAsComponent(String bundleKey, Locale locale) {
		return getBundleAsComponent(bundleKey, locale, new Object[0]);
	}
	
	/**
	 * Gets a string for the given key from resource bundle and try to transform it to a BaseComponent[] with ComponentSerializer.parse(String)
	 * Use the given locale for locale discover
	 * Use String.format on the returned string
	 * @param bundleKey
	 * @param locale
	 * @param args
	 * @return
	 */
	BaseComponent[] getBundleAsComponent(String bundleKey, Locale locale, Object... args);
	
	/**
	 * little helper to merge BaseComponent arrays
	 * @param components
	 * @return
	 */
	default BaseComponent[] mergeBaseComponents(BaseComponent[]... components) {
		int newLength = Arrays.stream(components).mapToInt(bc -> bc.length).sum();
		Vector<BaseComponent> result = new Vector<>(newLength);
		for (BaseComponent[] component : components) {
			Arrays.stream(component).forEach(result::add);
		}
		return result.toArray(new BaseComponent[newLength]);
	}
	
	// Send Message to defined User (UUID) if Online
	
	/**
	 * Send Message to User if Online, drop the Message elsewhere
	 * @param uuid
	 * @param message
	 */
	default void sendMessage(UUID uuid, BaseComponent[] message) {
		sendMessage(ChatMessageType.SYSTEM, uuid, message);
	}
	
	/**
	 * Send Message to User if Online, drop the Message elsewhere
	 * Gets a string for the given key from resource bundle
	 * Try to get Locale from UUID else use Locale.getDefault() for Locale discover
	 * Use String.format on the returned string
	 * @param uuid
	 * @param bundleKey
	 * @param args
	 */
	default void sendMessage(UUID uuid, String bundleKey, Object... args) {
		sendMessage(ChatMessageType.SYSTEM, uuid, bundleKey, args);
	}
	
	// Send Message to defined ChatMessageType for defined User (UUID) if Online
	
	/**
	 * Send Message to User if Online, drop the Message elsewhere
	 * Use the ChatMessageType to display the message
	 * @param position
	 * @param uuid
	 * @param message
	 */
	void sendMessage(ChatMessageType position, UUID uuid, BaseComponent[] message);
	
	/**
	 * Send Message to User if Online, drop the Message elsewhere
	 * Gets a string for the given key from resource bundle
	 * Try to get Locale from UUID else use Locale.getDefault() for Locale discover
	 * Use String.format on the returned string
	 * Use the ChatMessageType to display the message
	 * @param position
	 * @param uuid
	 * @param bundleKey
	 * @param args
	 */
	void sendMessage(ChatMessageType position, UUID uuid, String bundleKey, Object... args);
	
	// Broadcast Message

	/**
	 * Broadcast a Message to all Players with a given Permission
	 * @param position Chat Message Type
	 * @param permission Permission or null for all Players
	 * @param bundleKey
	 * @param args
	 */
	void sendMessageToAll(ChatMessageType position, String permission, String bundleKey, Object... args);
	
	// Send Debug Messages
	
	default void sendDebugMessage(UUID uuid, String bundleKey, Object... args) {
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(uuid, bundleKey, args);
		}
	}
	
	default void sendDebugMessage(ChatMessageType position, UUID uuid, String bundleKey, Object... args) {
		SessionObject so = SessionAPI.getSessionObject(uuid);
		if (so.contains(SessionKeys.Debug)) {
			sendMessage(position, uuid, bundleKey, args);
		}
	}
	
}
