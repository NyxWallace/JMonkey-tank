package ch.supsi.gamedev.tank3d.controls.kinematiccontrols;

import ch.supsi.gamedev.tank3d.controls.ProjectileControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.AltimeterControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.NozzleFlashControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class KinematicTankControl extends AbstractControl implements TankControl, Cloneable {

	// Default
	private static final float DEFAULT_ALTITUDE = 3.0f;
	private static final float DEFAULT_FORWARD_SPEED = 15.0f;
	private static final float DEFAULT_STRAFE_SPEED = 10.0f;
	private static final float DEFAULT_BACKWARD_SPEED = 10.0f;
	private static final float DEFAULT_STEERING_SPEED = 3.0f;
	private static final float DEFAULT_TURRET_SPEED = 8.0f;
	private static final float DEFAULT_CANNON_SPEED = 8.0f;
	private static final float DEFAULT_RELOAD_TIME = 0.0f;
	// Properties
	private float altitude = DEFAULT_ALTITUDE;
	private float forwardSpeed = DEFAULT_FORWARD_SPEED; // WU/sec
	private float strafeSpeed = DEFAULT_STRAFE_SPEED; // WU/sec
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
	private Vector2f throttle = new Vector2f(); // -1..1
	private float steering = 0.0f; // -1..1
	private float turretDeltaAngle = 0.0f;
	private float cannonDeltaElevation = 0.0f;
	private boolean firing = false;
	private boolean initialized = false;
	// SceneGraph
	private Node tank = null;
	private Node turret = null;
	private Node cannon = null;
	private Node nozzle = null;
	private AltimeterControl altimeterControl = null;
	private NozzleFlashControl nozzleFlashControl = null;

	private void initialize() {
		Node nozzleFlashEffect = Utils.getChild(nozzle, "nozzleFlash", Node.class);
		altimeterControl = tank != null ? tank.getControl(AltimeterControl.class) : null;
		nozzleFlashControl = nozzleFlashEffect != null ? nozzleFlashEffect.getControl(NozzleFlashControl.class) : null;
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (tank == null || turret == null || cannon == null || nozzle == null) {
			return;
		}
		if (!initialized) {
			initialize();
			return;
		}

		// Get state
		Vector3f tankPosition = tank.getLocalTranslation();
		Vector3f tankWorldPosition = tank.getWorldTranslation();
		Vector3f tankLeft = tank.getLocalRotation().getRotationColumn(0);
		Vector3f tankUp = tank.getLocalRotation().getRotationColumn(1);
		Vector3f tankAhead = tank.getLocalRotation().getRotationColumn(2);
		float currentAltitude = altitude;
		if (altimeterControl != null && altimeterControl.isValid()) {
			currentAltitude = altimeterControl.getAltitude();
		}

		// Compute deltas
		Vector2f deltaPosition2 = new Vector2f(throttle);
		deltaPosition2.multLocal(new Vector2f(strafeSpeed, throttle.getY() < 0.0f ? backwardSpeed : forwardSpeed));
		deltaPosition2.multLocal(tpf);
		Vector3f deltaPosition = new Vector3f();
		deltaPosition.addLocal(tankAhead.mult(deltaPosition2.getY()));
		deltaPosition.subtractLocal(tankLeft.mult(deltaPosition2.getX()));
		deltaPosition.setY(altitude - currentAltitude);
		float deltaAngle = -steering * steeringSpeed * tpf;

		turretDeltaAngle = FastMath.clamp(turretDeltaAngle, -turretSpeed * tpf, turretSpeed * tpf);
		cannonDeltaElevation = FastMath.clamp(cannonDeltaElevation, -cannonSpeed * tpf, cannonSpeed * tpf);

		// Update state
		tankPosition.addLocal(deltaPosition);
		angle += deltaAngle;
		turretAngle += turretDeltaAngle;
		cannonElevation += cannonDeltaElevation;
		lastTimeFired += tpf;

		angle %= FastMath.TWO_PI;
		turretAngle %= FastMath.TWO_PI;
		cannonElevation = FastMath.clamp(cannonElevation, -15.0f * FastMath.DEG_TO_RAD, 45.0f * FastMath.DEG_TO_RAD);

		tank.setLocalTranslation(tankPosition);
		tank.setLocalRotation(new Quaternion(new float[]{0.0f, angle, 0.0f}));
		turret.setLocalRotation(new Quaternion(new float[]{0.0f, turretAngle, 0.0f}));
		cannon.setLocalRotation(new Quaternion(new float[]{-cannonElevation, 0.0f, 0.0f}));

		if (firing && lastTimeFired >= reloadTime) {
			SpawnControl projectileSpawnControl = nozzle.getControl(SpawnControl.class);
			if (projectileSpawnControl != null) {
				Spatial projectile = projectileSpawnControl.spawn("Have a nice day!");
				ProjectileControl projectileControl = projectile.getControl(ProjectileControl.class);
				if (projectileControl != null) {
					projectileControl.fire();
				}
				if (nozzleFlashControl != null) {
					nozzleFlashControl.fire();
				}
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
	public Listable listable() {
		return null;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
		cannon = Utils.getChild(turret, "cannon", Node.class);
		nozzle = Utils.getChild(cannon, "nozzle", Node.class);
	}

	public float getForwardSpeed() {
		return forwardSpeed;
	}

	public void setForwardSpeed(float forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	public float getBackwardSpeed() {
		return backwardSpeed;
	}

	public void setBackwardSpeed(float backwardSpeed) {
		this.backwardSpeed = backwardSpeed;
	}

	public float getSteeringSpeed() {
		return steeringSpeed;
	}

	public void setSteeringSpeed(float steeringSpeed) {
		this.steeringSpeed = steeringSpeed;
	}

	@Override
	public float getTurretSpeed() {
		return turretSpeed;
	}

	@Override
	public void setTurretSpeed(float turretSpeed) {
		this.turretSpeed = turretSpeed;
	}

	@Override
	public float getCannonSpeed() {
		return cannonSpeed;
	}

	@Override
	public void setCannonSpeed(float cannonSpeed) {
		this.cannonSpeed = cannonSpeed;
	}

	@Override
	public float getReloadTime() {
		return reloadTime;
	}

	@Override
	public void setReloadTime(float reloadTime) {
		this.reloadTime = FastMath.clamp(reloadTime, 0.0f, Float.MAX_VALUE);
	}

	@Override
	public Vector2f getThrottle() {
		return throttle;
	}

	@Override
	public void setThrottle(Vector2f throttle) {
		throttle.setX(FastMath.clamp(throttle.getX(), -1.0f, 1.0f));
		throttle.setY(FastMath.clamp(throttle.getY(), -1.0f, 1.0f));
		this.throttle = throttle;
	}

	@Override
	public float getSteering() {
		return steering;
	}

	@Override
	public void setSteering(float steering) {
		this.steering = FastMath.clamp(steering, -1.0f, 1.0f);
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

	@Override
	public void applyDamage(float damage) {
	}

	@Override
	public void die() {
	}

	@Override
	public Vector3f getPosition() {
		return tank.getLocalTranslation();
	}

	@Override
	public void setPosition(Vector3f position) {
		tank.setLocalTranslation(position);
	}

	@Override
	public Quaternion getOrientation() {
		return tank.getLocalRotation();
	}

	@Override
	public void setOrientation(Quaternion orientation) {
		tank.setLocalRotation(orientation);
	}

	@Override
	public float getTurretAngle() {
		return turretAngle;
	}

	@Override
	public void setTurretAngle(float turretAngle) {
		this.turretAngle = turretAngle;
	}

	@Override
	public float getCannonElevation() {
		return cannonElevation;
	}

	@Override
	public void setCannonElevation(float cannonElevation) {
		this.cannonElevation = cannonElevation;
	}

	@Override
	public Vector3f getVelocity() {
		return null;
	}

	@Override
	public void setVelocity(Vector3f velocity) {
	}

	@Override
	public Vector3f getAngularVelocity() {
		return null;
	}

	@Override
	public void setAngularVelocity(Vector3f angularVelocity) {
	}

	@Override
	public boolean canFire() {
		return lastTimeFired >= reloadTime;
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		altitude = capsule.readFloat("altitude", DEFAULT_ALTITUDE);
		forwardSpeed = capsule.readFloat("forwardSpeed", DEFAULT_FORWARD_SPEED);
		strafeSpeed = capsule.readFloat("strafeSpeed", DEFAULT_STRAFE_SPEED);
		backwardSpeed = capsule.readFloat("backwardSpeed", DEFAULT_BACKWARD_SPEED);
		steeringSpeed = capsule.readFloat("steeringSpeed", DEFAULT_STEERING_SPEED);
		turretSpeed = capsule.readFloat("turretSpeed", DEFAULT_TURRET_SPEED);
		cannonSpeed = capsule.readFloat("cannonSpeed", DEFAULT_CANNON_SPEED);
		reloadTime = capsule.readFloat("reloadTime", DEFAULT_RELOAD_TIME);
		angle = capsule.readFloat("angle", 0.0f);
		turretAngle = capsule.readFloat("turretAngle", 0.0f);
		cannonElevation = capsule.readFloat("cannonElevation", 0.0f);
		lastTimeFired = capsule.readFloat("lastTimeFired", 0.0f);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(altitude, "altitude", DEFAULT_ALTITUDE);
		capsule.write(forwardSpeed, "forwardSpeed", DEFAULT_FORWARD_SPEED);
		capsule.write(strafeSpeed, "strafeSpeed", DEFAULT_STRAFE_SPEED);
		capsule.write(backwardSpeed, "backwardSpeed", DEFAULT_BACKWARD_SPEED);
		capsule.write(steeringSpeed, "steeringSpeed", DEFAULT_STEERING_SPEED);
		capsule.write(turretSpeed, "turretSpeed", DEFAULT_TURRET_SPEED);
		capsule.write(cannonSpeed, "cannonSpeed", DEFAULT_CANNON_SPEED);
		capsule.write(reloadTime, "reloadTime", DEFAULT_RELOAD_TIME);
		capsule.write(angle, "angle", 0.0f);
		capsule.write(turretAngle, "turretAngle", 0.0f);
		capsule.write(cannonElevation, "cannonElevation", 0.0f);
		capsule.write(lastTimeFired, "lastTimeFired", 0.0f);
	}
}
