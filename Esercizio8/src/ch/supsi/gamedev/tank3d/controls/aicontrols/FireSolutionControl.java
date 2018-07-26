package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class FireSolutionControl extends AbstractControl implements Cloneable {

	private static float DEFAULT_PROJECTILE_VELOCITY = 200.0f; // WU/sec
	// Properties
	private float projectileVelocity = DEFAULT_PROJECTILE_VELOCITY; // WU/sec
	// Transients
	private Vector3f targetLastPosition = null;
	private float gravity = 9.81f;
	private float azimuth = 0.0f;
	private float elevation = 0.0f;
	private float range = 0.0f;
	private boolean visible = false;
	private boolean valid = false;
	// SceneGraph
	private Spatial target = null;
	private Node turret = null;
	private TankControl tankControl = null;
	private boolean initialized = false;

	private boolean isTargetVisible() {
		Node tank = spatial instanceof Node ? (Node) spatial : null;
		Node targetNode = target instanceof Node ? (Node) target : null;
		if (tank == null ||targetNode == null ) {
			return false;
		}
		Vector3f position = turret.getWorldTranslation();
		Vector3f targetPosition = target.getWorldTranslation();
		Vector3f vector = targetPosition.subtract(position);
		Vector3f versor = vector.normalize();
		Ray ray = new Ray(position, versor);
		Node rootNode = Globals.SINGLETON.getRootNode();

		CollisionResults collisionResults = new CollisionResults();
		rootNode.collideWith(ray, collisionResults);
		for (CollisionResult collisionResult : collisionResults) {
			Geometry geometry = collisionResult.getGeometry();
			String name = geometry.getName();
			if ("sky".equals(name)) {
				continue;
			}
			if (tank.hasChild(geometry)) {
				continue;
			}
			return targetNode.hasChild(geometry);
		}
		return true;
	}

	private float computeAzimuth(Vector2f targetVector, Vector2f targetVelocity, float time) {
		Vector2f vector = targetVector.add(targetVelocity.mult(time));
		return vector.getAngle();
	}

	private float computeElevation(float distance, float deltaHeight) throws Exception {
		double v2 = projectileVelocity * projectileVelocity;
		double v4 = v2 * v2;
		double x = distance;
		double x2 = distance * distance;
		double y = deltaHeight;
		double g = gravity;
		double d = v4 - g * (g * x2 + 2 * y * v2);
		if (g == 0.0f || x == 0.0f || d < 0.0f) {
			throw new Exception();
		}
		return (float) Math.atan((v2 - Math.sqrt(d)) / g / x);
	}

	private float computeTimeOfTravel(float distance, float elevation) {
		return distance / projectileVelocity / FastMath.cos(elevation);
	}

	private float computeTimeOfTravel(Vector2f targetVector, Vector2f targetVelocity, float projectilePlaneVelocity) throws Exception {
		double a = targetVelocity.dot(targetVelocity) - projectilePlaneVelocity * projectilePlaneVelocity;
		double b = 2.0f * targetVector.dot(targetVelocity);
		double c = targetVector.dot(targetVector);
		double d = b * b - 4.0f * a * c;
		if (a == 0.0 || d < 0.0) {
			throw new Exception();
		}
		double t1 = (-b - Math.sqrt(b * b - a * c * 4.0)) / a / 2.0;
		double t2 = (-b + Math.sqrt(b * b - a * c * 4.0)) / a / 2.0;
		return t1 > 0.0 ? (float) t1 : (float) t2;
	}

	private float computeTargetDistance(float time, float startDistance, float distanceVelocity) {
		return startDistance + distanceVelocity * time;
	}

	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
			return;
		}

		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		BulletAppState bulletAppState = stateManager != null ? stateManager.getState(BulletAppState.class) : null;
		if (bulletAppState == null) {
			return;
		}
		Vector3f gravityVector = new Vector3f();
		bulletAppState.getPhysicsSpace().getGravity(gravityVector);
		gravity = -gravityVector.getY();

		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		if (target == null) {
			valid = false;
			return;
		}

		Vector3f position = turret.getWorldTranslation();
		Vector3f velocity = tankControl.getVelocity();
		Vector3f targetPosition = new Vector3f(target.getWorldTranslation());
		Vector3f targetVelocity = Vector3f.ZERO;

		RigidBodyControl rigidBodyControl = target.getControl(RigidBodyControl.class);
		if (rigidBodyControl != null) {
			targetVelocity = rigidBodyControl.getLinearVelocity();
		} else if (targetLastPosition != null && tpf > 0.0f) {
			targetVelocity = targetPosition.subtract(targetLastPosition).divide(tpf);
		}
		targetLastPosition = targetPosition;

		Vector3f vector = targetPosition.subtract(position);
		Vector2f planeVector = new Vector2f(vector.getZ(), vector.getX());
		Vector2f planeDirection = planeVector.normalize();
		float startDistance = planeVector.length();
		float deltaHeight = vector.getY();

		Vector3f deltaVelocity = targetVelocity.subtract(velocity);
		Vector2f planeDeltaVelocity = new Vector2f(deltaVelocity.getZ(), deltaVelocity.getX());
		float distanceVelocity = planeDeltaVelocity.dot(planeDirection);

		try {
			float targetDistance = startDistance;
			for (int iteration = 0; iteration < 8; iteration++) {
				elevation = computeElevation(targetDistance, deltaHeight);
				float time = computeTimeOfTravel(targetDistance, elevation);
				targetDistance = computeTargetDistance(time, startDistance, distanceVelocity);
			}
		} catch (Exception exception) {
			valid = false;
			return;
		}

		float planeProjectileVelocity = FastMath.cos(elevation) * projectileVelocity;
		try {
			float time = computeTimeOfTravel(planeVector, planeDeltaVelocity, planeProjectileVelocity);
			azimuth = computeAzimuth(planeVector, planeDeltaVelocity, time);
		} catch (Exception exception) {
			valid = false;
			return;
		}
		range = vector.length();
		azimuth *= FastMath.RAD_TO_DEG;
		elevation *= FastMath.RAD_TO_DEG;
		visible = isTargetVisible();
		valid = true;
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
	}

	public Spatial getTarget() {
		return target;
	}

	public void setTarget(Spatial target) {
		this.target = target;
	}

	public float getRange() {
		return range;
	}

	public float getAzimuth() {
		return azimuth;
	}

	public float getElevation() {
		return elevation;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isValid() {
		return valid;
	}
}
