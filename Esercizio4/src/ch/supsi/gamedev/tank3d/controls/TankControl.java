package ch.supsi.gamedev.tank3d.controls;

import com.jme3.scene.control.Control;

public interface TankControl extends Control {

	// Properties
	public float getForwardSpeed(); // WU/sec

	public void setForwardSpeed(float forwardSpeed);

	public float getBackwardSpeed(); // WU/sec

	public void setBackwardSpeed(float backwardSpeed);

	public float getSteeringSpeed(); // RAD/sec

	public void setSteeringSpeed(float steeringSpeed);

	public float getTurretSpeed(); // RAD/sec

	public void setTurretSpeed(float turretSpeed);

	public float getCannonSpeed(); // RAD/sec

	public void setCannonSpeed(float cannonSpeed);

	public float getReloadTime(); // sec

	public void setReloadTime(float reloadTime);

	// Controls
	public float getThrottle();

	public void setThrottle(float throttle); // -1..1

	public float getSteering();

	public void setSteering(float steering); // -1..1

	public void rotateTurret(float turretDeltaAngle);

	public void rotateCannon(float cannonDeltaElevation);

	public void fire();
}
