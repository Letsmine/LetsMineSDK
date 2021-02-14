package eu.letsmine.sdk;

import java.util.Locale;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public interface IMessageCMD<CMD> extends IMessages {
	
	Locale getLocale(CMD sender);
	
	default void sendMessage(CMD sender, BaseComponent[] message) {
		sendMessage(ChatMessageType.SYSTEM, sender, message);
	}
	
	void sendMessage(ChatMessageType position, CMD sender, BaseComponent[] message);
	
	default void sendMessage(CMD sender, String cmdBundleKey, Object... args) {
		sendMessage(ChatMessageType.SYSTEM, sender, cmdBundleKey, args);
	}
	
	void sendMessage(ChatMessageType position, CMD sender, String cmdBundleKey, Object... args);
	
	default void sendNoPermissionMessage(CMD sender) {
		sendMessage(ChatMessageType.SYSTEM, sender, "eu.letsmine.noPermission");
	}
	
	void sendDebugMessage(CMD sender, String bundleKey, Object... args);
	
	void sendDebugMessage(ChatMessageType position, CMD sender, String bundleKey, Object... args);

}
