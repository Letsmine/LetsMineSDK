package eu.letsmine.sdk;

import java.util.Locale;
import java.util.UUID;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public interface IMessageCMDProxy<T extends IMessageCMD<CMD>, CMD> extends IMessageCMD<CMD> {
	
	T getMessageProxy();
	
	String getClassName();
	
	default Locale getLocale(CMD sender) {
		return getMessageProxy().getLocale(sender);
	}
	
	default String getProxyBundleKey(String key) {
		return getClassName() + "." + key;
	}
	
	@Override
	default BaseComponent[] getBundleAsComponent(String key, Locale locale, Object... args) {
		return getMessageProxy().getBundleAsComponent(getProxyBundleKey(key), locale, args);
	}
	
	@Override
	default String getBundleValue(String proxyBundleKey, Locale locale, Object... args) {
		return getMessageProxy().getBundleValue(getProxyBundleKey(proxyBundleKey), locale, args);
	}

	@Override
	default void sendMessage(ChatMessageType position, CMD sender, BaseComponent[] message) {
		getMessageProxy().sendMessage(position, sender, message);
	}
	
	@Override
	default void sendMessage(ChatMessageType position, CMD sender, String cmdBundleKey, Object... args) {
		getMessageProxy().sendMessage(position, sender, getBundleAsComponent(cmdBundleKey, getMessageProxy().getLocale(sender), args));
	}
	
	@Override
	default void sendMessage(ChatMessageType position, UUID uuid, BaseComponent[] message) {
		getMessageProxy().sendMessage(position, uuid, message);
	}
	
	@Override
	default void sendMessage(ChatMessageType position, UUID uuid, String cmdBundleKey, Object... args) {
		getMessageProxy().sendMessage(position, uuid, getProxyBundleKey(cmdBundleKey), args);
	}
	
	@Override
	default void sendMessageToAll(ChatMessageType position, String permission, String cmdBundleKey, Object... args) {
		getMessageProxy().sendMessageToAll(position, permission, getProxyBundleKey(cmdBundleKey), args);
	}

	@Override
	default void sendDebugMessage(CMD sender, String bundleKey, Object... args) {
		getMessageProxy().sendDebugMessage(sender, bundleKey, args);
	}

	@Override
	default void sendDebugMessage(ChatMessageType position, CMD sender, String bundleKey, Object... args) {
		getMessageProxy().sendDebugMessage(position, sender, bundleKey, args);
	}
	
}