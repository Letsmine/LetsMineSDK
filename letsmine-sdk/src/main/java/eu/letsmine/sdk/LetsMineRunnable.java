package eu.letsmine.sdk;

public abstract class LetsMineRunnable<T extends ILetsMinePlugin<CMD>, CMD> implements Runnable {
	
	protected final T plugin;
	
	public LetsMineRunnable(T plugin) {
		this.plugin = plugin;
	}
	
}
