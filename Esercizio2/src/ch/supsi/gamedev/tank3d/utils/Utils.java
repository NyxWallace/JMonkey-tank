package ch.supsi.gamedev.tank3d.utils;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Utils {

	public static Node getRootNode(Spatial spatial) {
		if (spatial == null) {
			return null;
		}
		Node parent = spatial.getParent();
		if (parent == null) {
			return spatial instanceof Node ? (Node) spatial : null;
		}
		return getRootNode(parent);
	}

	public static <T extends Spatial> T getChild(Node parent, String name, Class<T> childClass) {
		T result = null;
		Spatial child = parent != null ? parent.getChild(name) : null;
		if (child != null && childClass.isAssignableFrom(child.getClass())) {
			result = (T) child;
		}
		return result;
	}
	
	public static float clamp(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}
	
	public static Quaternion getVectorsRotation(Vector3f source, Vector3f target) {
		Quaternion result = new Quaternion();
		source = source.normalize();
		target = target.normalize();
		Vector3f axis = source.cross(target);
		float angle = FastMath.acos(source.dot(target));
		result.fromAngleAxis(angle, axis);
		return result;
	}
}
