package ch.supsi.gamedev.tank3d.controls.sensorcontrols;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class RangeControl extends AbstractControl implements Cloneable {
	
	//Global
	private static Node rootNode = null;

	public static Node getRootNode() {
		return rootNode;
	}

	public static void setRootNode(Node rootNode) {
		RangeControl.rootNode = rootNode;
	}

	private Vector3f point = null;
	private Vector3f normal = null;
	private float range = 0.0f;
	private boolean valid = false;
	
	@Override
	protected void controlUpdate(float tpf) {
		if (rootNode == null) {
			return;
		}
		Vector3f position = spatial.getWorldTranslation();
		Vector3f aheadVector = spatial.getWorldRotation().getRotationColumn(2);

		Ray ray = new Ray(position, aheadVector);
		CollisionResults collisionResults = new CollisionResults();
		rootNode.collideWith(ray, collisionResults);
		
		CollisionResult minCollisionResult = null;
		for (CollisionResult collisionResult : collisionResults) {
			Geometry geometry = collisionResult.getGeometry();
			String name = geometry.getName();
			if (!"sky".equals(name)) {
				minCollisionResult = collisionResult;
				break;
			}
		}
		if (minCollisionResult != null && !minCollisionResult.getContactNormal().equals(Vector3f.ZERO)) {
			point = minCollisionResult.getContactPoint();
			normal = minCollisionResult.getContactNormal();
			range = minCollisionResult.getDistance();
			valid = true;
		} else {
			valid = false;
		}
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

	public float getRange() {
		return range;
	}
}
