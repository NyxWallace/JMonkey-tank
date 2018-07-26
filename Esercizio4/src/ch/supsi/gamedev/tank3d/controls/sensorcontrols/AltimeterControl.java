package ch.supsi.gamedev.tank3d.controls.sensorcontrols;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class AltimeterControl extends AbstractControl implements Cloneable {

	//Global
	private static Node globalTerrain = null;

	public static Node getGlobalTerrain() {
		return globalTerrain;
	}

	public static void setGlobalTerrain(Node globalTerrain) {
		AltimeterControl.globalTerrain = globalTerrain;
	}
	// SceneGraph
	private boolean initialized = false;
	// Transients
	private Vector3f point = null;
	private Vector3f normal = null;
	private float altitude = 0.0f;
	private boolean valid = false;

	private CollisionResult collisionResult(Vector3f vector) {
		Vector3f spatialPosition = spatial.getWorldTranslation();
		Ray ray = new Ray(spatialPosition, vector);
		CollisionResults collisionResults = new CollisionResults();
		globalTerrain.collideWith(ray, collisionResults);
		return collisionResults.getClosestCollision();
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (globalTerrain == null) {
			return;
		}

		CollisionResult collisionResult;
		Vector3f upVector = Vector3f.UNIT_Y;
		collisionResult = collisionResult(upVector.negate());
		
		if (collisionResult != null && !collisionResult.getContactNormal().equals(Vector3f.ZERO)) {
			point = collisionResult.getContactPoint();
			normal = collisionResult.getContactNormal();
			altitude = collisionResult.getDistance();
			valid = true;
			return;
		}
	
		collisionResult = collisionResult(upVector);
		if (collisionResult != null && !collisionResult.getContactNormal().equals(Vector3f.ZERO)) {
			point = collisionResult.getContactPoint();
			normal = collisionResult.getContactNormal();
			altitude = -collisionResult.getDistance();
			valid = true;
			return;
		}
		
		valid = false;
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public boolean isValid() {
		return valid;
	}

	public Vector3f getPoint() {
		return point;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public float getAltitude() {
		return altitude;
	}
}
