package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class KinematicTankControl extends AbstractControl implements TankControl, Cloneable {

	// Default
	private static final float DEFAULT_FORWARD_SPEED = 3.0f;
	private static final float DEFAULT_BACKWARD_SPEED = 2.0f;
	private static final float DEFAULT_STEERING_SPEED = 1.0f;
	private static final float DEFAULT_TURRET_SPEED = 6.0f;
	private static final float DEFAULT_CANNON_SPEED = 6.0f;
	private static final float DEFAULT_RELOAD_TIME = 3.0f;
	// Properties
	private float forwardSpeed = DEFAULT_FORWARD_SPEED; // WU/sec
	private float backwardSpeed = DEFAULT_BACKWARD_SPEED; // WU/sec
	private float steeringSpeed = DEFAULT_STEERING_SPEED; // WU/sec
	private float turretSpeed = DEFAULT_TURRET_SPEED; // RAD/sec
	private float cannonSpeed = DEFAULT_CANNON_SPEED; // RAD/sec
	private float reloadTime = DEFAULT_RELOAD_TIME; // WU/sec
	// State
	private float angle = 0.0f;
	private float turretAngle = 0.0f;
	private float cannonElevation = 0.0f; // -10°...45°
	private float lastTimeFired = 0.0f;
	// Transient
	private float throttle = 0.0f; // -1..1
	private float steering = 0.0f; // -1..1
	private float turretDeltaAngle = 0.0f;
	private float cannonDeltaElevation = 0.0f;
	private boolean firing = false;
	// SceneGraph
	private Node tank = null;
	private Node turret = null;
	private Node cannon = null;
	private Node nozzle = null;

	@Override
	protected void controlUpdate(float tpf) {
		if (tank == null || turret == null || cannon == null) {
			return;
		}

		// Get state
		Vector3f tankPosition = tank.getLocalTranslation();
		Vector3f tankWorldPosition = tank.getWorldTranslation();
		Vector3f tankLeft = tank.getLocalRotation().getRotationColumn(0);
		Vector3f tankUp = tank.getLocalRotation().getRotationColumn(1);
		Vector3f tankAhead = tank.getLocalRotation().getRotationColumn(2);

		// Compute deltas
		float deltaPosition1 = throttle * tpf * (throttle < 0.0f ? backwardSpeed : forwardSpeed);
		Vector3f deltaPosition = tankAhead.mult(deltaPosition1);
		float deltaAngle = -steering * steeringSpeed * tpf;
		
		turretDeltaAngle = Utils.clamp(turretDeltaAngle, -turretSpeed * tpf, turretSpeed * tpf);
		cannonDeltaElevation = Utils.clamp(cannonDeltaElevation, -cannonSpeed * tpf, cannonSpeed * tpf);

		// Update state
		tankPosition.addLocal(deltaPosition);
		angle += deltaAngle;
		turretAngle += turretDeltaAngle;
		cannonElevation += cannonDeltaElevation;
		lastTimeFired += tpf;
		

		angle %= FastMath.TWO_PI;
		turretAngle %= FastMath.TWO_PI;
		cannonElevation = Utils.clamp(cannonElevation, -15.0f * FastMath.DEG_TO_RAD, 45.0f * FastMath.DEG_TO_RAD);

		tank.setLocalTranslation(tankPosition);
		tank.setLocalRotation(new Quaternion(new float[]{0.0f, angle, 0.0f}));
		turret.setLocalRotation(new Quaternion(new float[]{0.0f, turretAngle, 0.0f}));
		cannon.setLocalRotation(new Quaternion(new float[]{-cannonElevation, 0.0f, 0.0f}));

		if (firing && lastTimeFired >= reloadTime) {
			SpawnControl projectileSpawnControl = nozzle.getControl(SpawnControl.class);
			if (projectileSpawnControl != null) {
				projectileSpawnControl.spawn("Have a nice day!");
			}
			lastTimeFired = 0.0f;
		}

		// Reset accumulators
		turretDeltaAngle = 0.0f;
		cannonDeltaElevation = 0.0f;
		firing = false;
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
		cannon = Utils.getChild(turret, "cannon", Node.class);
		nozzle = Utils.getChild(cannon, "nozzle", Node.class);
	}

	@Override
	public float getForwardSpeed() {
		return forwardSpeed;
	}

	@Override
	public void setForwardSpeed(float forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	@Override
	public float getBackwardSpeed() {
		return backwardSpeed;
	}

	@Override
	public void setBackwardSpeed(float backwardSpeed) {
		this.backwardSpeed = backwardSpeed;
	}

	@Override
	public float getSteeringSpeed() {
		return steeringSpeed;
	}

	@Override
	public void setSteeringSpeed(float steeringSpeed) {
		this.steeringSpeed = steeringSpeed;
	}

	@Override
	public float getTurretSpeed() {
		return steeringSpeed;
	}

	@Override
	public void setTurretSpeed(float turretSpeed) {
		this.turretSpeed = turretSpeed;
	}

	@Override
	public float getCannonSpeed() {
		return steeringSpeed;
	}

	@Override
	public void setCannonSpeed(float cannonSpeed) {
		this.cannonSpeed = cannonSpeed;
	}

	@Override
	public float getReloadTime() {
		return steeringSpeed;
	}

	@Override
	public void setReloadTime(float reloadTime) {
		this.reloadTime = Utils.clamp(throttle, 0.0f, Float.MAX_VALUE);
	}

	@Override
	public float getThrottle() {
		return steeringSpeed;
	}

	@Override
	public void setThrottle(float throttle) {
		this.throttle = Utils.clamp(throttle, -1.0f, 1.0f);
	}

	@Override
	public float getSteering() {
		return steeringSpeed;
	}

	@Override
	public void setSteering(float steering) {
		this.steering = Utils.clamp(steering, -1.0f, 1.0f);
	}

	@Override
	public void rotateTurret(float turretDeltaAngle) {
		this.turretDeltaAngle += turretDeltaAngle;
	}

	@Override
	public void rotateCannon(float cannonDeltaElevation) {
		this.cannonDeltaElevation += cannonDeltaElevation;
	}

	@Override
	public void fire() {
		if (lastTimeFired > reloadTime) {
			firing = true;
		}
	}
}
