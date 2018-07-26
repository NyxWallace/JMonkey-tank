package ch.supsi.gamedev.tank3d.controls;

import com.jme3.math.Vector2f;
import com.jme3.scene.control.Control;

public interface TankControl extends Control {

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
}
