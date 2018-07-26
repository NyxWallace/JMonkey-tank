package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class StabilityControl extends AbstractControl implements Cloneable {
	
	// States
	private float azimuth = 0.0f; // DEG
	private float elevation = 0.0f; // DEG
	// Transients
	private float error = 0.0f;
	// SceneGraph
	private Node turret = null;
	private Node cannon = null;
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
		
		float xAngle = elevation * FastMath.DEG_TO_RAD;
		float yAngle = azimuth * FastMath.DEG_TO_RAD;
		Quaternion xRotation = new Quaternion(new float[] {-xAngle, 0.0f, 0.0f});
		Quaternion yRotation = new Quaternion(new float[] {0.0f, yAngle, 0.0f});
		Vector3f worldDirection = yRotation.mult(xRotation.mult(Vector3f.UNIT_Z));

		Quaternion worldToTankRotation = spatial.getWorldRotation().inverse();
		Vector3f localAzimuthDirection = worldToTankRotation.mult(worldDirection);
		Vector2f localAzimuthDirection2 = new Vector2f(localAzimuthDirection.getZ(), localAzimuthDirection.getX());
		
		Quaternion worldToTurretRotation = turret.getWorldRotation().inverse();
		Vector3f localElevationDirection = worldToTurretRotation.mult(worldDirection);
		Vector2f localElevationDirection2 = new Vector2f(localElevationDirection.getZ(), localElevationDirection.getY());
		
		float localyAngle = Utils.normalizeAngle(localAzimuthDirection2.getAngle());
		float localxAngle =  Utils.normalizeAngle(localElevationDirection2.getAngle());
		float currentLocalyAngle = Utils.normalizeAngle(tankControl.getTurretAngle());
		float currentLocalxAngle = tankControl.getCannonElevation();
		float deltaLocalyAngle = Utils.deltaAngle(currentLocalyAngle, localyAngle);
		float deltaLocalxAngle = Utils.deltaAngle(currentLocalxAngle, localxAngle);
		tankControl.rotateTurret(deltaLocalyAngle);
		tankControl.rotateCannon(deltaLocalxAngle);
		Vector3f currentWorldDirection = cannon.getWorldRotation().getRotationColumn(2);
		error = worldDirection.angleBetween(currentWorldDirection);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node tank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(tank, "turret", Node.class);
		cannon = Utils.getChild(tank, "cannon", Node.class);
	}

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = Utils.normalizeAngleDeg(azimuth);
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = Utils.normalizeAngleDeg(elevation);
	}

	public float getError() {
		return error;
	}
	
	public void rotateTurret(float deltaAzimuth) {
		azimuth += deltaAzimuth;
		azimuth = Utils.normalizeAngleDeg(azimuth);
	}

	public void rotateCannon(float deltaElevation) {
		elevation += deltaElevation;
		elevation = Utils.normalizeAngleDeg(elevation);
	}
}
