package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.controls.effectcontrols.ExplosionControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class KinematicProjectileControl extends AbstractControl implements ProjectileControl, Cloneable {

	// Default
	private final static float DEFAULT_START_VELOCITY = 200.0f;
	private final static float DEFAULT_DAMPING = 0.3f;
	private final static float DEFAULT_GRAVITY = 50.0f;
	// Properties
	private float startVelocity = DEFAULT_START_VELOCITY;
	private float damping = DEFAULT_DAMPING;
	private float gravity = DEFAULT_GRAVITY;
	// State
	private Vector3f velocity = null;
	private boolean firing = false;
	//Transient
	private boolean exploding = false;
	//SceneGraph
	private SpawnControl explosionSpawnControl = null;
	private boolean initialized = false;
	
	private void initialize() {
		Node node = spatial instanceof Node ? (Node) spatial : null;
		Node warhead = Utils.getChild(node, "warhead", Node.class);
		explosionSpawnControl = warhead != null ? warhead.getControl(SpawnControl.class) : null;
		if (explosionSpawnControl == null) {
			return;
		}
		
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}

		if (!firing) {
			return;
		}
		
		// Get state
		Vector3f position = spatial.getLocalTranslation();
		Vector3f aheadVector = spatial.getLocalRotation().getRotationColumn(2);
		if (velocity == null) {
			velocity = aheadVector.mult(startVelocity);
		}

		// Compute deltas
		Vector3f deltaPosition = velocity.mult(tpf);
		Vector3f deltaVelocity = new Vector3f();
		deltaVelocity.addLocal(Vector3f.UNIT_Y.mult(-gravity * tpf));
		deltaVelocity.subtractLocal(velocity.mult(1.0f - FastMath.pow(1.0f - damping, tpf)));
		
		// Update state
		if (exploding || position.getY() < 0.0f) {
			Spatial explosion = explosionSpawnControl.spawn("explosion");
			ExplosionControl explosionControl = explosion != null ? explosion.getControl(ExplosionControl.class) : null;
			if (explosionControl != null) {
				explosionControl.fire();
			}
			spatial.removeFromParent();
		}
	
		spatial.move(deltaPosition);
		spatial.getLocalRotation().lookAt(velocity, Vector3f.UNIT_Y);
		velocity.addLocal(deltaVelocity);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
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
	public void fire() {
		firing = true;
	}

	@Override
	public void explode() {
		exploding = true;
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		startVelocity = capsule.readFloat("startVelocity", DEFAULT_START_VELOCITY);
		damping = capsule.readFloat("damping", DEFAULT_DAMPING);
		gravity = capsule.readFloat("gravity", DEFAULT_GRAVITY);
		firing = capsule.readBoolean("firing", false);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(startVelocity, "startVelocity", DEFAULT_START_VELOCITY);
		capsule.write(damping, "damping", DEFAULT_DAMPING);
		capsule.write(gravity, "gravity", DEFAULT_GRAVITY);
		capsule.write(firing, "firing", false);
	}
}
