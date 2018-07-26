package ch.supsi.gamedev.tank3d.events;

import ch.supsi.gamedev.tank3d.Event;
import com.jme3.scene.Spatial;

public class TankEvent extends Event {

	public static enum Type {

		DAMAGED("tank damaged"), DEAD("tank dead");
		private final String id;

		private Type(String id) {
			this.id = id;
		}
	}
	private final Type type;
	private final Spatial tank;

	public TankEvent(Type type, Spatial tank) {
		super(type.id);
		this.type = type;
		this.tank = tank;
	}

	public Type type() {
		return type;
	}

	public Spatial tank() {
		return tank;
	}

	@Override
	public String toString() {
		return type.id + " " + tank;
	}
}
