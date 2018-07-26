package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.Event;

public abstract class Behaviour {
	
	private BehavioursManager manager = null;

	public BehavioursManager getManager() {
		return manager;
	}

	public void setManager(BehavioursManager manager) {
		this.manager = manager;
	}
	
	public abstract void start();
	
	public abstract void loop(float tpf);
	
	public abstract void end();
	
	public abstract void event(Event event);
}

