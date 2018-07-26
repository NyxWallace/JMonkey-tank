package ch.supsi.gamedev.tank3d.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class KinematicProjectileControl extends AbstractControl implements Cloneable {

	// Default
	private static final float DEFAULT_SPEED = 128.0f;
	private static final float DEFAULT_RANGE = 256.0f;
	// Properties
	private float speed = DEFAULT_SPEED;
	private float range = DEFAULT_RANGE;
	// State
	private float ranDistance = 0.0f;

	@Override
	protected void controlUpdate(float tpf) {

		// Get state
		Vector3f position = spatial.getLocalTranslation();
		Vector3f aheadVector = spatial.getLocalRotation().getRotationColumn(2);

		// Compute deltas
		float deltaPosition1 = speed * tpf;
		Vector3f deltaPosition = aheadVector.mult(deltaPosition1);

		// Update state
		spatial.move(deltaPosition);
		ranDistance += deltaPosition1;

		if (ranDistance > range) {
			spatial.removeFromParent();
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}
}
