package ch.supsi.gamedev.tank3d.utils;

import java.util.HashSet;
import java.util.Set;

public abstract class Listable<ThisListener extends Listener> {
	
	private final Set<ThisListener> listeners = new HashSet<>();
	
	protected Set<ThisListener> listeners() {
		return listeners;
	}
	
	public void addListener(ThisListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ThisListener listener) {
		listeners.remove(listener);
	}
}
