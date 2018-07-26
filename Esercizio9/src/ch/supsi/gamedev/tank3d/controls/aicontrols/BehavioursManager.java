package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.Event;
import java.util.HashMap;
import java.util.Map;

public class BehavioursManager {

	private final Map<Class<? extends Behaviour>, Behaviour> behaviours = new HashMap<>();
	private Behaviour currentBehaviour = null;

	public void addBehaviour(Behaviour behaviour) {
		behaviours.put(behaviour.getClass(), behaviour);
	}

	public <ThisBehaviour extends Behaviour> ThisBehaviour getBehaviour(Class<ThisBehaviour> behaviourClass) {
		return (ThisBehaviour) behaviours.get(behaviourClass);
	}
	
	public boolean setCurrentBehaviour(Class<? extends Behaviour> behaviourClass) {
		Behaviour behaviour = getBehaviour(behaviourClass);
		return behaviour != null && setCurrentBehaviour(behaviour);
	}

	public boolean setCurrentBehaviour(Behaviour currentBehaviour) {
		if (!behaviours.containsValue(currentBehaviour)) {
			return false;
		}
		if (currentBehaviour != null ? !currentBehaviour.equals(this.currentBehaviour) : this.currentBehaviour != null) {
			if (this.currentBehaviour != null) {
				this.currentBehaviour.end();
				this.currentBehaviour.setManager(null);
			}
			this.currentBehaviour = currentBehaviour;
			if (currentBehaviour != null) {
				currentBehaviour.setManager(this);
				currentBehaviour.start();
			}
		}
		return true;
	}

	public void update(float tpf) {
		if (currentBehaviour != null) {
			currentBehaviour.loop(tpf);
		}
	}

	public void clear() {
		behaviours.clear();
		if (currentBehaviour != null) {
			currentBehaviour.setManager(null);
			currentBehaviour.end();
		}
		currentBehaviour = null;
	}
	
	public void event(Event event) {
		if (currentBehaviour != null) {
			currentBehaviour.event(event);
		}
	}
}
