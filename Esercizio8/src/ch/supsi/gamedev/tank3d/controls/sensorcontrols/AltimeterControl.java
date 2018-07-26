package ch.supsi.gamedev.tank3d.controls.sensorcontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class AltimeterControl extends AbstractControl implements Cloneable {

	// Defaults
	private static final boolean DEFAULT_RELATIVE = true;
	// Properties
	private boolean relative = DEFAULT_RELATIVE;
	// Transients
	private Vector3f point = null;
	private Vector3f normal = null;
	private float altitude = 0.0f;
	private boolean valid = false;
	// SceneGraph
	private Node rootNode = null;
	private Node terrain = null;
	private boolean initialized = false;
	
	private void initialize() {
		terrain = Utils.getChild(rootNode, "terrain", Node.class);
		if (terrain == null) {
			return;
		}
		initialized = true;
	}

	private CollisionResult collisionResult(Vector3f vector) {
		Vector3f spatialPosition = spatial.getWorldTranslation();
		Ray ray = new Ray(spatialPosition, vector);
		CollisionResults collisionResults = new CollisionResults();
		terrain.collideWith(ray, collisionResults);
		return collisionResults.getClosestCollision();
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}

		CollisionResult collisionResult;
		Vector3f upVector = relative ? spatial.getWorldRotation().getRotationColumn(1) : Vector3f.UNIT_Y;
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

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		rootNode = Globals.SINGLETON.getRootNode();
	}

	public boolean isRelative() {
		return relative;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;
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
