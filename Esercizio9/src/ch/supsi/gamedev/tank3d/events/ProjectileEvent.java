package ch.supsi.gamedev.tank3d.events;

import ch.supsi.gamedev.tank3d.Event;
import com.jme3.scene.Spatial;

public class ProjectileEvent extends Event {

	public static enum Type {

		FIRED("projectile fired"), EXPLODED("projectile exploded");
		private final String id;

		private Type(String id) {
			this.id = id;
		}
	}
	private final Type type;
	private final Spatial projectile;
	private final Spatial tank;

	public ProjectileEvent(Type type, Spatial projectile, Spatial tank) {
		super(type.id);
		this.type = type;
		this.projectile = projectile;
		this.tank = tank;
	}
	
	public Type type() {
		return type;
	}
	
	
	public Spatial projectile() {
		return projectile;
	}
	
	public Spatial tank() {
		return tank;
	}

	@Override
	public String toString() {
		return type.id + " " + projectile + " fired by " + tank;
	}
}
