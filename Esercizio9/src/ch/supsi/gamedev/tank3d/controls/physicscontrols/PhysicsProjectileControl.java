package ch.supsi.gamedev.tank3d.controls.physicscontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.controls.listeners.CollisionListener;
import ch.supsi.gamedev.tank3d.controls.listeners.PhysicsListener;
import ch.supsi.gamedev.tank3d.controls.ProjectileControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.ExplosionControl;
import ch.supsi.gamedev.tank3d.events.ProjectileEvent;
import ch.supsi.gamedev.tank3d.events.TankEvent;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class PhysicsProjectileControl extends AbstractControl implements ProjectileControl, PhysicsListener, CollisionListener, Cloneable {

	private static enum State {

		IDLE, FIRING, PHYSICS_FIRING, RUNNING, EXPLODING;
	}
	// Default
	private final static float DEFAULT_START_VELOCITY = 400.0f;
	private final static float DEFAULT_IMPACT_IMPULSE = 100000.0f;
	private final static float DEFAULT_IMPACT_DAMAGE = 60000.0f;
	// Properties
	private float startVelocity = DEFAULT_START_VELOCITY;
	private float impactImpulse = DEFAULT_IMPACT_IMPULSE;
	private float impactDamage = DEFAULT_IMPACT_DAMAGE;
	// State
	private State state = State.IDLE;
	private Spatial owner = null;
	private float remainingTime = 0.0f;
	// Transients
	private Vector3f driftVelocity = null;
	//AppStates
	private EventsManager eventsManager = null;
	private AudioManager audioManager = null;
	//SceneGraph
	private Node warhead = null;
	private PhysicsSpace physicsSpace = null;
	private RigidBodyControl rigidBodyControl = null;
	private SpawnControl explosionSpawnControl = null;
	private AudioNode runAudio = null;
	private AudioNode explosionAudio = null;
	private boolean initialized = false;

	private void initialize() {
		rigidBodyControl = spatial.getControl(RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}
		explosionSpawnControl = warhead != null ? warhead.getControl(SpawnControl.class) : null;
		if (explosionSpawnControl == null) {
			return;
		}

		rigidBodyControl.setCcdMotionThreshold(1.0f);
		rigidBodyControl.setCcdSweptSphereRadius(1.0f);
		CylinderCollisionShape cylinderCollisionShape = new CylinderCollisionShape(new Vector3f(0.25f, 0.25f, 0.875f), 2);
		SphereCollisionShape sphereCollisionShape = new SphereCollisionShape(0.25f);
		CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
		compoundCollisionShape.addChildShape(cylinderCollisionShape, new Vector3f(0.0f, 0.0f, 0.0f));
		compoundCollisionShape.addChildShape(sphereCollisionShape, new Vector3f(0.0f, 0.0f, 0.875f));
		rigidBodyControl.setCollisionShape(compoundCollisionShape);
		initialized = true;
	}

	private void playAudioInstance() {
		if (audioManager != null) {
			audioManager.addSound(explosionAudio);
		} else if (Globals.SINGLETON.hasAudioRenderer()) {
			explosionAudio.playInstance();
		}
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		switch (state) {
			case FIRING:
				if (Globals.SINGLETON.hasAudioRenderer()) {
					runAudio.play();
				}
				remainingTime = 5.0f;
				state = State.PHYSICS_FIRING;
				break;
			case RUNNING:
				Vector3f velocity = rigidBodyControl.getLinearVelocity();
				float factor = Utils.normalize(velocity.length(), 50.0f, 200.0f);
				float volume = factor;
				float pitch = factor * 1.5f + 0.5f;
				runAudio.setVolume(volume);
				runAudio.setPitch(pitch);
				runAudio.setVelocity(velocity);

				remainingTime -= tpf;
				if (remainingTime < 0.0f) {
					explode();
				}
				break;
			case EXPLODING:
				if (Globals.SINGLETON.hasAudioRenderer()) {
					runAudio.stop();
				}
				Spatial explosion = explosionSpawnControl.spawn("explosion");
				ExplosionControl explosionControl = explosion != null ? explosion.getControl(ExplosionControl.class) : null;
				if (explosionControl != null) {
					explosionControl.fire();
				}
				spatial.removeFromParent();
				break;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		eventsManager = stateManager != null ? stateManager.getState(EventsManager.class) : null;
		audioManager = stateManager != null ? stateManager.getState(AudioManager.class) : null;
		Node projectile = spatial instanceof Node ? (Node) spatial : null;
		warhead = Utils.getChild(projectile, "warhead", Node.class);
		runAudio = Utils.getChild(projectile, "runAudio", AudioNode.class);
		explosionAudio = Utils.getChild(warhead, "explosionAudio", AudioNode.class);
	}

	public boolean isRunning() {
		return state == State.RUNNING;
	}

	@Override
	public Spatial getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Spatial owner) {
		this.owner = owner;
	}

	@Override
	public float getStartVelocity() {
		return startVelocity;
	}

	@Override
	public void setStartVelocity(float startVelocity) {
		this.startVelocity = startVelocity;
	}

	@Override
	public void setDriftVelocity(Vector3f driftVelocity) {
		this.driftVelocity = new Vector3f(driftVelocity);
	}

	@Override
	public void fire() {
		if (state != State.IDLE) {
			return;
		}
		if (eventsManager != null) {
			eventsManager.addEvent(new ProjectileEvent(ProjectileEvent.Type.FIRED, spatial, owner));
		}
		state = State.FIRING;
	}

	@Override
	public void explode() {
		if (state != State.RUNNING) {
			return;
		}
		if (eventsManager != null) {
			eventsManager.addEvent(new ProjectileEvent(ProjectileEvent.Type.EXPLODED, spatial, owner));
		}
		state = State.EXPLODING;
	}

	@Override
	public void physicsUpdate(float tpf) {
		if (!initialized) {
			return;
		}
		Vector3f aheadVector = rigidBodyControl.getPhysicsRotation().getRotationColumn(2);
		switch (state) {
			case PHYSICS_FIRING:
				float mass = rigidBodyControl.getMass();
				Vector3f impulse = aheadVector.mult(startVelocity * mass);
				if (driftVelocity != null) {
					impulse.addLocal(driftVelocity.mult(mass));
				}
				rigidBodyControl.applyImpulse(impulse, Vector3f.ZERO);
				state = State.RUNNING;
				break;
			case RUNNING:
				Vector3f flyingDirection = rigidBodyControl.getLinearVelocity().normalize();
				Vector3f axis = aheadVector.cross(flyingDirection).normalize();
				float angle = flyingDirection.angleBetween(aheadVector);
				float speed = rigidBodyControl.getLinearVelocity().length();
				rigidBodyControl.applyTorque(axis.mult(speed * angle * 1.0f));
				break;
		}
	}

	@Override
	public void collision(Spatial spatial, Vector3f point, Vector3f normal) {
		if (state != State.RUNNING) {
			return;
		}
		explode();
		RigidBodyControl spatialRigidBodyControl = spatial.getControl(RigidBodyControl.class);
		if (spatialRigidBodyControl != null) {
			spatialRigidBodyControl.applyImpulse(normal.negate().mult(impactImpulse), point);
		}

		TankControl tankControl = spatial.getControl(TankControl.class);
		if (tankControl != null) {
			tankControl.applyDamage(impactDamage);
			playAudioInstance();
			if (eventsManager != null) {
				eventsManager.addEvent(new TankEvent(TankEvent.Type.DAMAGED, spatial));
			}
		}
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		startVelocity = capsule.readFloat("startVelocity", DEFAULT_START_VELOCITY);
		impactImpulse = capsule.readFloat("impactImpulse", DEFAULT_IMPACT_IMPULSE);
		impactDamage = capsule.readFloat("impactDamage", DEFAULT_IMPACT_DAMAGE);
		state = capsule.readEnum("altitude", State.class, State.IDLE);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(startVelocity, "startVelocity", DEFAULT_START_VELOCITY);
		capsule.write(impactImpulse, "impactImpulse", DEFAULT_IMPACT_IMPULSE);
		capsule.write(impactDamage, "impactDamage", DEFAULT_IMPACT_DAMAGE);
		capsule.write(state, "state", State.IDLE);
	}
}
