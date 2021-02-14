package eu.letsmine.sdk;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public interface ILetsMinePlugin<CMD> extends IMessageCMD<CMD> {
	
	public static final String netherSuffix = "_nether";
	public static final String endSuffix = "_the_end";
	
	public static WorldNameInfo wrapWorldName(String originalWorldName) {
		WorldNameInfo worldName;
		if (originalWorldName.endsWith(ILetsMinePlugin.netherSuffix)) {
			worldName = new WorldNameInfo(originalWorldName.substring(0, originalWorldName.length() - ILetsMinePlugin.netherSuffix.length()), true, false);
		} else if (originalWorldName.endsWith(ILetsMinePlugin.endSuffix)) {
			worldName = new WorldNameInfo(originalWorldName.substring(0, originalWorldName.length() - ILetsMinePlugin.endSuffix.length()), false, true);
		} else {
			worldName = new WorldNameInfo(originalWorldName, false, false);
		}
		return worldName;
	}
	
	@Data
	@AllArgsConstructor
	public static class WorldNameInfo {
		private final String worldName;
		private final boolean nether, end;
	}

	UUID getSystemUUID();
	
	UUID getUUID(CMD sender);
	
	Locale getLocale(UUID uuid);
	
	Locale getLocale(CMD sender);
	
	/**
	 * Gets the Bundle for this Plugin
	 * @return
	 */
	default ResourceBundle getBundle() {
		return getBundle(Locale.getDefault());
	}
	
	/**
	 * Gets the Bundle for this Plugin for given Locale
	 * @param locale
	 * @return
	 */
	ResourceBundle getBundle(Locale locale);
	
	/**
	 * Gets the Bundle for this Let's Mine SDK for given Locale
	 * @param locale
	 * @return
	 */
	ResourceBundle getSDKBundle(Locale locale);

	@Override
	default BaseComponent[] getBundleAsComponent(String key, Locale locale) {
		return ComponentSerializer.parse(getBundleValue(key, locale));
	}

	@Override
	default BaseComponent[] getBundleAsComponent(String key, Locale locale, Object... args) {
		return ComponentSerializer.parse(getBundleValue(key, locale, args));
	}
	
	@Override
	default String getBundleValue(String key, Locale locale) {
		return getBundleValue(key, locale, new Object[0]);
	}
	
	/**
	 * Gets the Bundle Value after String.format() with given Locale
	 * @param key
	 * @param locale
	 * @param args
	 * @return resource String or key
	 */
	@Override
	default String getBundleValue(String key, Locale locale, Object... args) {
		
		String bundleValue;
		Object[] format;
		
		if (BundleKeys.Dummy.equals(key)) {
			if (args.length <= 0 || !(args[0] instanceof String)) {
				bundleValue = key;
				format = args;
			} else {
				bundleValue = (String)args[0];
				format = Arrays.copyOfRange(args, 1, args.length); // ToDo: testen ob das korrekt ist oder ob noch -1 genommen werden muss...
			}
		} else {
			format = args;
			
			ResourceBundle rb = getBundle(locale);
			if (!rb.containsKey(key)) {
				rb = getSDKBundle(locale);
			}
			
			if (rb == null || !rb.containsKey(key)) {
				bundleValue = key;
			} else {
				bundleValue = rb.getString(key);
			}
		}
		convertBundleValueArgs(format);
		
		return String.format(bundleValue, args);
	}
	
	/**
	 * Convert diverent types to redable formates for getBungeValue
	 * @param obj
	 */
	default void convertBundleValueArgs(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			if (obj instanceof BaseComponent[]) {
				BaseComponent[] bc = (BaseComponent[])obj;
				objects[i] = ComponentSerializer.toString( bc );
			}
		}
	}

}
