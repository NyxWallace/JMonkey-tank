package ch.supsi.gamedev.tank3d.controls;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

public interface TankControl extends Control {
	
	public static class Listable extends ch.supsi.gamedev.tank3d.utils.Listable<Listener> {
		
		public void fireFired(String projectileName) {
			for (Listener listener : listeners()) {
				listener.fired(projectileName);
			}
		}
		
		public void fireDamaged(float damage) {
			for (Listener listener : listeners()) {
				listener.damaged(damage);
			}
		}
	
		public void fireDied() {
			for (Listener listener : listeners()) {
				listener.died();
			}
		}
	}
	
	public static interface Listener extends ch.supsi.gamedev.tank3d.utils.Listener {
		
		public void fired(String projectileName);
		
		public void damaged(float damage);
		
		public void died();
	}
	
	public Listable listable();

	// Properties
	public float getTurretSpeed(); // RAD/sec

	public void setTurretSpeed(float turretSpeed);

	public float getCannonSpeed(); // RAD/sec

	public void setCannonSpeed(float cannonSpeed);

	public float getReloadTime(); // sec

	public void setReloadTime(float reloadTime);

	// Controls
	public Vector2f getThrottle();

	public void setThrottle(Vector2f throttle); // -1..1 / -1..1

	public float getSteering();

	public void setSteering(float steering); // -1..1

	public void rotateTurret(float turretDeltaAngle);

	public void rotateCannon(float cannonDeltaElevation);

	public void fire();
	
	public void applyDamage(float damage);
	
	public void die();
	
	// State
	public Vector3f getPosition();
	
	public void setPosition(Vector3f position);
	
	public Quaternion getOrientation();
	
	public void setOrientation(Quaternion orientation);
	
	public float getTurretAngle();
	
	public void setTurretAngle(float turretAngle);
	
	public float getCannonElevation();
	
	public void setCannonElevation(float cannonElevation);
	
	public Vector3f getVelocity();
	
	public void setVelocity(Vector3f velocity);
	
	public Vector3f getAngularVelocity();
	
	public void setAngularVelocity(Vector3f angularVelocity);
	
	public boolean canFire();
}
