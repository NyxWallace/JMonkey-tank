package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.controls.ProjectileControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.TankControl.Listable;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.DustControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.NozzleFlashControl;
import ch.supsi.gamedev.tank3d.controls.listeners.PhysicsListener;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.AntiGravityControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.DeadTankControl;
import ch.supsi.gamedev.tank3d.events.TankEvent;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
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
import java.util.Set;

public class PlayerTankControl extends AbstractControl implements TankControl, PhysicsListener, Cloneable {

	//Defaults
	private static final float DEFAULT_FORWARD_FORCE = 70000.0f; // N
	private static final float DEFAULT_STRAFE_FORCE = 50000.0f; // N
	private static final float DEFAULT_BACKWARD_FORCE = 30000.0f; // N
	private static final float DEFAULT_TURN_TORQUE = 100000.0f; // Nm
	private static final float DEFAULT_TURRET_SPEED = 3.0f; // RAD/sec
	private static final float DEFAULT_CANNON_SPEED = 3.0f; // RAD/sec
	private static final float DEFAULT_CANNON_IMPULSE = 30000.0f; // Ns
	private static final float DEFAULT_RELOAD_TIME = 3.0f; // sec
	//Properties
	private float forwardForce = DEFAULT_FORWARD_FORCE; // N
	private float strafeForce = DEFAULT_STRAFE_FORCE; // N
	private float backwardForce = DEFAULT_BACKWARD_FORCE; // N
	private float turnTorque = DEFAULT_TURN_TORQUE; // Nm
	private float turretSpeed = DEFAULT_TURRET_SPEED; // RAD/sec
	private float cannonSpeed = DEFAULT_CANNON_SPEED; // RAD/sec
	private float cannonImpulse = DEFAULT_CANNON_IMPULSE; // Ns
	private float reloadTime = DEFAULT_RELOAD_TIME; // WU/sec
	//State
	private float turretAngle = 0.0f;
	private float cannonElevation = 0.0f;
	private float lastTimeFired = 0.0f;
	private int firedProjectilesCount = 0;
	//Transients
	private Vector2f throttle = new Vector2f(); // -1..1 / -1..1
	private float steering = 0.0f; // -1..1
	private float turretDeltaAngle = 0.0f;
	private float cannonDeltaElevation = 0.0f;
	private float damage = 0.0f;
	private boolean firing = false;
	private boolean physicsFiring = false;
	private boolean dead = false;
	//SceneGraph
	private EventsManager eventsManager = null;
	private Node tank = null;
	private Node turret = null;
	private Node cannon = null;
	private Node nozzle = null;
	private RigidBodyControl rigidBodyControl = null;
	private NozzleFlashControl nozzleFlashControl = null;
	private SpawnControl deadTankSpawnControl = null;
	private SpawnControl projectileSpawnControl = null;
	private DustControl dustControl = null;
	private Set<AntiGravityControl> antiGravityControls = null;
	private boolean initialized = false;
	private Listable listable = null;

	private void initialize() {
		Node nozzleFlash = Utils.getChild(nozzle, "nozzleFlash", Node.class);
		rigidBodyControl = tank.getControl(RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}
		nozzleFlashControl = nozzleFlash != null ? nozzleFlash.getControl(NozzleFlashControl.class) : null;
		if (nozzleFlashControl == null) {
			return;
		}

		deadTankSpawnControl = tank.getControl(SpawnControl.class);
		if (deadTankSpawnControl == null) {
			return;
		}

		projectileSpawnControl = nozzle != null ? nozzle.getControl(SpawnControl.class) : null;
		if (projectileSpawnControl == null) {
			return;
		}

		Node dust = Utils.getChild(tank, "dust", Node.class);
		dustControl = dust != null ? dust.getControl(DustControl.class) : null;
		antiGravityControls = Utils.descendantsControls(spatial, AntiGravityControl.class);

		CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 1.1f, 6.0f)), new Vector3f(0.0f, 0.25f, 0.0f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 1.1f, 6.0f)), new Vector3f(0.0f, 0.25f, 0.0f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 1.5f)), new Vector3f(-3.5f, -0.25f, -2.5f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 1.5f)), new Vector3f(3.5f, -0.25f, -2.5f));
		compoundCollisionShape.addChildShape(new CylinderCollisionShape(new Vector3f(1.75f, 0.5f, 1.75f), 1), new Vector3f(0.0f, 2.0f, -2.36f));

		rigidBodyControl.setCollisionShape(compoundCollisionShape);
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (tank == null || turret == null || cannon == null) {
			return;
		}
		if (!initialized) {
			initialize();
			return;
		}

		turretDeltaAngle = FastMath.clamp(turretDeltaAngle, -turretSpeed * tpf, turretSpeed * tpf);
		cannonDeltaElevation = FastMath.clamp(cannonDeltaElevation, -cannonSpeed * tpf, cannonSpeed * tpf);

		turretAngle += turretDeltaAngle;
		cannonElevation += cannonDeltaElevation;
		lastTimeFired += tpf;

		turretAngle %= FastMath.TWO_PI;
		cannonElevation = FastMath.clamp(cannonElevation, -15.0f * FastMath.DEG_TO_RAD, 45.0f * FastMath.DEG_TO_RAD);

		turret.setLocalRotation(new Quaternion(new float[]{0.0f, turretAngle, 0.0f}));
		cannon.setLocalRotation(new Quaternion(new float[]{-cannonElevation, 0.0f, 0.0f}));

		if (firing && lastTimeFired >= reloadTime) {
			Spatial projectile;
			String projectileName = spatial.getName() + ".projectile" + firedProjectilesCount;
			projectile = projectileSpawnControl.spawn(projectileName);
			ProjectileControl projectileControl = projectile.getControl(ProjectileControl.class);
			if (projectileControl != null) {
				projectileControl.setOwner(spatial);
				projectileControl.setDriftVelocity(rigidBodyControl.getLinearVelocity());
				projectileControl.fire();
			}
			if (nozzleFlashControl != null) {
				nozzleFlashControl.fire();
			}
			lastTimeFired = 0.0f;
			firing = false;
			physicsFiring = true;
			firedProjectilesCount++;
			listable.fireFired(projectileName);
		}

		turretDeltaAngle = 0.0f;
		cannonDeltaElevation = 0.0f;

		if (dustControl != null && !antiGravityControls.isEmpty()) {
			float intensity = 0.0f;
			for (AntiGravityControl antiGravityControl : antiGravityControls) {
				intensity += antiGravityControl.getThrottle();
			}
			intensity /= (float) antiGravityControls.size();
			intensity *= 4.0f;
			intensity = FastMath.clamp(intensity, 0.0f, 1.0f);
			dustControl.setIntensity(intensity);
		}

		Object healthObject = spatial.getUserData("health");
		if (healthObject instanceof Float) {
			Float health = (Float) healthObject;
			health -= damage;
			spatial.setUserData("health", health);
			if (damage > 0.0f) {
				listable.fireDamaged(damage);
				damage = 0.0f;
			}
		}
		if (dead) {
			Spatial deadTank = deadTankSpawnControl.spawn("deadTank");
			DeadTankControl deadTankControl = deadTank.getControl(DeadTankControl.class);
			if (deadTankControl != null) {
				deadTankControl.setOriginalTank(tank);
			}
			tank.removeFromParent();
			if (eventsManager != null) {
				eventsManager.addEvent(new TankEvent(TankEvent.Type.DEAD, spatial));
			}
			listable.fireDied();
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public Listable listable() {
		return listable;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		eventsManager = stateManager != null ? stateManager.getState(EventsManager.class) : null;
		tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
		cannon = Utils.getChild(turret, "cannon", Node.class);
		nozzle = Utils.getChild(cannon, "nozzle", Node.class);
		listable = new Listable();
	}

	public float getForwardForce() {
		return forwardForce;
	}

	public void setForwardForce(float forwardForce) {
		this.forwardForce = forwardForce;
	}

	public float getStrafeForce() {
		return strafeForce;
	}

	public void setStrafeForce(float strafeForce) {
		this.strafeForce = strafeForce;
	}

	public float getBackwardForce() {
		return backwardForce;
	}

	public void setBackwardForce(float backwardForce) {
		this.backwardForce = backwardForce;
	}

	public float getTurnTorque() {
		return turnTorque;
	}

	public void setTurnTorque(float turnTorque) {
		this.turnTorque = turnTorque;
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

	public float getCannonImpulse() {
		return cannonImpulse;
	}

	public void setCannonImpulse(float cannonImpulse) {
		this.cannonImpulse = cannonImpulse;
	}

	@Override
	public float getReloadTime() {
		return reloadTime;
	}

	@Override
	public void setReloadTime(float reloadTime) {
		this.reloadTime = reloadTime;
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
		steering = FastMath.clamp(steering, -1.0f, 1.0f);
		this.steering = steering;
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

	public void applyRemoteDamage(float remoteDamage) {
		this.damage += damage;
	}

	@Override
	public void die() {
		dead = true;
	}

	@Override
	public Vector3f getPosition() {
		return rigidBodyControl.getPhysicsLocation();
	}

	@Override
	public void setPosition(Vector3f position) {
		rigidBodyControl.setPhysicsLocation(position);
	}

	@Override
	public Quaternion getOrientation() {
		return rigidBodyControl.getPhysicsRotation();
	}

	@Override
	public void setOrientation(Quaternion orientation) {
		rigidBodyControl.setPhysicsRotation(orientation);
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
		return rigidBodyControl.getLinearVelocity();
	}

	@Override
	public void setVelocity(Vector3f velocity) {
		rigidBodyControl.setLinearVelocity(velocity);
	}

	@Override
	public Vector3f getAngularVelocity() {
		return rigidBodyControl.getAngularVelocity();
	}

	@Override
	public void setAngularVelocity(Vector3f angularVelocity) {
		rigidBodyControl.setAngularVelocity(angularVelocity);
	}

	@Override
	public boolean canFire() {
		return lastTimeFired >= reloadTime;
	}

	@Override
	public void physicsUpdate(float tpf) {
		if (!initialized) {
			return;
		}
		Vector3f leftVector = spatial.getWorldRotation().getRotationColumn(0);
		Vector3f upVector = spatial.getWorldRotation().getRotationColumn(1);
		Vector3f aheadVector = spatial.getWorldRotation().getRotationColumn(2);
		Vector2f force2 = new Vector2f(throttle);
		force2.multLocal(new Vector2f(strafeForce, throttle.getY() < 0.0f ? backwardForce : forwardForce));
		Vector3f force = new Vector3f();
		force.addLocal(aheadVector.mult(force2.getY()));
		force.addLocal(leftVector.mult(-force2.getX()));
		float torque1 = turnTorque * -steering;
		Vector3f torque = upVector.mult(torque1);
		rigidBodyControl.applyCentralForce(force);
		rigidBodyControl.applyTorque(torque);
		if (physicsFiring) {
			Vector3f nozzleAheadVector = nozzle.getWorldRotation().getRotationColumn(2);
			Vector3f nozzlePosition = nozzle.getWorldTranslation();
			Vector3f rigidBodyPosition = rigidBodyControl.getPhysicsLocation();
			Vector3f vector = nozzlePosition.subtract(rigidBodyPosition);
			Vector3f impulse = nozzleAheadVector.mult(-cannonImpulse);
			rigidBodyControl.applyImpulse(impulse, vector);
			physicsFiring = false;
		}
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		forwardForce = capsule.readFloat("forwardForce", DEFAULT_FORWARD_FORCE);
		strafeForce = capsule.readFloat("strafeForce", DEFAULT_STRAFE_FORCE);
		backwardForce = capsule.readFloat("backwardForce", DEFAULT_BACKWARD_FORCE);
		turnTorque = capsule.readFloat("turnTorque", DEFAULT_TURN_TORQUE);
		turretSpeed = capsule.readFloat("turretSpeed", DEFAULT_TURRET_SPEED);
		cannonSpeed = capsule.readFloat("cannonSpeed", DEFAULT_CANNON_SPEED);
		reloadTime = capsule.readFloat("reloadTime", DEFAULT_RELOAD_TIME);
		turretAngle = capsule.readFloat("turretAngle", 0.0f);
		cannonElevation = capsule.readFloat("cannonElevation", 0.0f);
		lastTimeFired = capsule.readFloat("lastTimeFired", 0.0f);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(forwardForce, "forwardForce", DEFAULT_FORWARD_FORCE);
		capsule.write(strafeForce, "strafeForce", DEFAULT_STRAFE_FORCE);
		capsule.write(backwardForce, "backwardForce", DEFAULT_BACKWARD_FORCE);
		capsule.write(turnTorque, "turnTorque", DEFAULT_TURN_TORQUE);
		capsule.write(turretSpeed, "turretSpeed", DEFAULT_TURRET_SPEED);
		capsule.write(cannonSpeed, "cannonSpeed", DEFAULT_CANNON_SPEED);
		capsule.write(reloadTime, "reloadTime", DEFAULT_RELOAD_TIME);
		capsule.write(turretAngle, "turretAngle", 0.0f);
		capsule.write(cannonElevation, "cannonElevation", 0.0f);
		capsule.write(lastTimeFired, "lastTimeFired", 0.0f);
	}
}
