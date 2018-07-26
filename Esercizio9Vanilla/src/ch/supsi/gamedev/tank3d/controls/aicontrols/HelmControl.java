package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class HelmControl extends AbstractControl implements Cloneable {

	public static enum Speed {

		BACK_FULL(-1.0f), BACK_HALF(-0.5f), STOP(0.0f), SLOW(0.3f), CRUISE(0.7f), FULL(1.0f);
		private final float throttle;

		private Speed(float throttle) {
			this.throttle = throttle;
		}
		
		public float throttle() {
			return throttle;
		}
	}
	// State
	private float heading = 0.0f; // DEG clock-wise
	private Speed speed = Speed.STOP;
	// SceneGraph
	private TankControl tankControl = null;
	private boolean initialized = false;

	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
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

		Vector3f aheadVector = spatial.getWorldRotation().getRotationColumn(2);
		Vector2f aheadVector2 = new Vector2f(aheadVector.getZ(), -aheadVector.getX());
		Vector2f leftVector2 = new Vector2f(aheadVector.getX(), aheadVector.getZ());

		float angle = heading * FastMath.DEG_TO_RAD;
		float currentAngle = Utils.normalizeAngle(aheadVector2.getAngle());
		float deltaAngle = Utils.deltaAngle(currentAngle, angle);

		float currentAngularVelocity = -tankControl.getAngularVelocity().getY();
		float angularVelocity = (Utils.normalize(deltaAngle, -FastMath.PI, FastMath.PI) - 0.5f) * 2.0f * 6.0f;
		float deltaAngularVelocity = angularVelocity - currentAngularVelocity;

		float steering = (Utils.normalize(deltaAngularVelocity, -0.3f, 0.3f) - 0.5f) * 2.0f * 1.0f;
		tankControl.setSteering(steering);

		Vector3f velocity = tankControl.getVelocity();
		Vector2f velocity2 = new Vector2f(velocity.getZ(), -velocity.getX());
		float currentForwardVelocity = aheadVector2.dot(velocity2);
		float currentLeftVelocity = leftVector2.dot(velocity2);

		float lateralThrottole = (Utils.normalize(currentLeftVelocity, 0.3f, -0.3f) - 0.5f) * 2.0f * 1.0f;
		float forwardThrottle;
		if (speed == Speed.STOP) {
			forwardThrottle = (Utils.normalize(currentForwardVelocity, 0.3f, -0.3f) - 0.5f) * 2.0f * 1.0f;
		} else {
			forwardThrottle = speed != null ? speed.throttle() : 0.0f;
		}
		tankControl.setThrottle(new Vector2f(lateralThrottole, forwardThrottle));
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getHeading() {
		return heading;
	}

	public void setHeading(float heading) {
		this.heading = Utils.normalizeAngleDeg(heading);
	}
	
	public float getCurrentHeading() {
		Vector3f aheadVector = spatial.getWorldRotation().getRotationColumn(2);
		Vector2f aheadVector2 = new Vector2f(aheadVector.getZ(), -aheadVector.getX());
		float currentAngle = Utils.normalizeAngle(aheadVector2.getAngle());
		return currentAngle * FastMath.RAD_TO_DEG;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
}
