package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.controls.ProjectileControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.DustControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.NozzleFlashControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.AntiGravityControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.DeadTankControl;
import ch.supsi.gamedev.tank3d.events.TankEvent;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Set;

public class RemoteTankControl extends AbstractControl implements TankControl, Cloneable {

	private Listable listable = null;
	// States
	private Vector3f velocity = null;
	// Transients
	private float damage = 0.0f;
	private boolean firing = false;
	private boolean dead = false;
	private String projectileName = "Have a nice day!";
	// SceneGraph
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

	private void initialize() {
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		eventsManager = stateManager != null ? stateManager.getState(EventsManager.class) : null;
		if (eventsManager == null) {
			return;
		}

		tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
		cannon = Utils.getChild(turret, "cannon", Node.class);
		nozzle = Utils.getChild(cannon, "nozzle", Node.class);
		if (tank == null || turret == null || cannon == null || nozzle == null) {
			return;
		}

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
		if (!initialized) {
			initialize();
			return;
		}
		if (firing) {
			Spatial projectile;
			projectile = projectileSpawnControl.spawn(projectileName);
			ProjectileControl projectileControl = projectile.getControl(ProjectileControl.class);
			if (projectileControl != null) {
				projectileControl.setOwner(spatial);
				projectileControl.fire();
			}
			if (nozzleFlashControl != null) {
				nozzleFlashControl.fire();
			}
			firing = false;
			listable.fireFired(projectileName);
		}

		Object healthObject = spatial.getUserData("health");
		if (healthObject instanceof Float) {
			Float health = (Float) healthObject;
			health -= damage;
			spatial.setUserData("health", health);
			if (health <= 0.0f) {
				dead = true;
			}
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
			listable.fireDied();
			if (eventsManager != null) {
				eventsManager.addEvent(new TankEvent(TankEvent.Type.DEAD, spatial));
			}
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		listable = new Listable();
	}

	@Override
	public TankControl.Listable listable() {
		return listable;
	}

	@Override
	public float getTurretSpeed() {
		return 0.0f;
	}

	@Override
	public void setTurretSpeed(float turretSpeed) {
	}

	@Override
	public float getCannonSpeed() {
		return 0.0f;
	}

	@Override
	public void setCannonSpeed(float cannonSpeed) {
	}

	@Override
	public float getReloadTime() {
		return 0.0f;
	}

	@Override
	public void setReloadTime(float reloadTime) {
	}

	@Override
	public Vector2f getThrottle() {
		return null;
	}

	@Override
	public void setThrottle(Vector2f throttle) {
	}

	@Override
	public float getSteering() {
		return 0.0f;
	}

	@Override
	public void setSteering(float steering) {
	}

	@Override
	public void rotateTurret(float turretDeltaAngle) {
	}

	@Override
	public void rotateCannon(float cannonDeltaElevation) {
	}

	@Override
	public void fire() {
		firing = true;
	}
	
	@Override
	public void applyDamage(float damage) {
	}
	
	public void applyRemoteDamage(float remoteDamage) {
		this.damage += remoteDamage;
	}

	@Override
	public void die() {
		dead = true;
	}

	@Override
	public Vector3f getPosition() {
		return null;
	}

	@Override
	public void setPosition(Vector3f position) {
	}

	@Override
	public Quaternion getOrientation() {
		return null;
	}

	@Override
	public void setOrientation(Quaternion orientation) {
	}

	@Override
	public float getTurretAngle() {
		return 0.0f;
	}

	@Override
	public void setTurretAngle(float turretAngle) {
	}

	@Override
	public float getCannonElevation() {
		return 0.0f;
	}

	@Override
	public void setCannonElevation(float cannonElevation) {
	}

	@Override
	public Vector3f getVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	@Override
	public Vector3f getAngularVelocity() {
		return null;
	}

	@Override
	public void setAngularVelocity(Vector3f angularVelocity) {
	}

	public String getProjectileName() {
		return projectileName;
	}

	public void setProjectileName(String projectileName) {
		this.projectileName = projectileName;
	}
	
	@Override
	public boolean canFire() {
		return true;
	}
}