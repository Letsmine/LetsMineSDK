package eu.letsmine.sdk;

import net.md_5.bungee.api.ChatMessageType;

public interface IMessageCMDProxyCommand<T extends IMessageCMD<CMD>, CMD> extends IMessageCMDProxy<T, CMD> {
	
	default void sendUsageMessage(CMD sender) {
		sendMessage(ChatMessageType.SYSTEM, sender, "usage");
	}
	
	boolean hasCommandPermission(CMD sender, String... args);
	
}