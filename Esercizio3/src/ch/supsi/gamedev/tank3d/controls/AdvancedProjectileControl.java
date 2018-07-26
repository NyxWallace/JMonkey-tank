package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class AdvancedProjectileControl extends AbstractControl {

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

	@Override
	protected void controlUpdate(float tpf) {

		// Get state
		Vector3f position = spatial.getLocalTranslation();
		Vector3f aheadVector = spatial.getLocalRotation().getRotationColumn(2);
		if (velocity == null) {
			velocity = aheadVector.mult(startVelocity);
		}

		// Compute deltas
		Vector3f deltaPosition = velocity.mult(tpf);
		Quaternion deltaRotation = Utils.getVectorsRotation(aheadVector, velocity);
		Vector3f deltaVelocity = new Vector3f();
		deltaVelocity.addLocal(Vector3f.UNIT_Y.mult(-gravity * tpf));
		deltaVelocity.subtractLocal(velocity.mult(1.0f - FastMath.pow(1.0f - damping, tpf)));

		// Update state
		spatial.move(deltaPosition);
		spatial.rotate(deltaRotation);
		velocity.addLocal(deltaVelocity);
		
		if (position.getY() < 0.0f) {
			spatial.removeFromParent();
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
}
