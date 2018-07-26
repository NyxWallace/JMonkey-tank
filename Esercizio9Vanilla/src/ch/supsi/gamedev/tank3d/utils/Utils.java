package ch.supsi.gamedev.tank3d.utils;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

	public static String[] getSpatialPath(Spatial spatial) {
		List<String> list = new ArrayList<>();
		while (spatial != null) {
			list.add(spatial.getName());
			spatial = spatial.getParent();
		}
		Collections.reverse(list);
		return list.toArray(new String[]{});
	}
	
	public static Spatial getSpatial(Spatial spatial, String[] path) {
		if (path.length <= 0) {
			return null;
		}
		String rootName = path[0];
		if (!rootName.equals(spatial.getName())) {
			return null;
		}
		Spatial result = spatial;
		for (int index = 1; index < path.length; index++) {
			String name = path[index];
			if (result instanceof Node) {
				Node node = (Node) result;
				result = node.getChild(name);
			}
		}
		return result;
	}
	
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
	
	public static <T extends Spatial> Set<T> descendants(Spatial spatial, final Class<T> spatialClass) {
		final Set<T> results = new HashSet<>();

		SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				if (spatialClass.isAssignableFrom(spatial.getClass())) {
					results.add((T) spatial);
				}
			}
		};
		spatial.depthFirstTraversal(sceneGraphVisitor);
		return results;
	}

	public static <T extends Control> Set<T> descendantsControls(Spatial spatial, final Class<T> controlClass) {
		final Set<T> results = new HashSet<>();

		SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				int controlsCount = spatial.getNumControls();
				for (int index = 0; index < controlsCount; index++) {
					Control control = spatial.getControl(index);
					if (controlClass.isAssignableFrom(control.getClass())) {
						results.add((T) control);
					}
				}
			}
		};
		spatial.depthFirstTraversal(sceneGraphVisitor);
		return results;
	}

	public static Set<Spatial> descendantsWithControl(Spatial spatial, final Class<? extends Control> controlClass) {
		final Set<Spatial> results = new HashSet<>();

		SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				Control control = spatial.getControl(controlClass);
				if (control != null) {
					results.add(spatial);
				}
			}
		};
		spatial.depthFirstTraversal(sceneGraphVisitor);
		return results;
	}

	public static <T extends Control> T getAncestorControl(Spatial spatial, Class<T> controlClass) {
		T result = null;
		Node parent = spatial != null ? spatial.getParent() : null;
		while (result == null && parent != null) {
			result = parent.getControl(controlClass);
			parent = parent.getParent();
		}
		return result;
	}

	public static void traverseUpwards(Spatial spatial, SceneGraphVisitor sceneGraphVisitor, int maxRecursions) {
		int recursion = 0;
		while (spatial != null && recursion < maxRecursions) {
			sceneGraphVisitor.visit(spatial);
			spatial = spatial.getParent();
			recursion++;
		}
	}

	public static Vector3f randomUnitVector() {
		Vector3f result = new Vector3f();
		float norm;
		do {
			for (int i = 0; i < 3; i++) {
				result.set(i, (FastMath.rand.nextFloat() * 2.0f - 1.0f));
			}
			norm = result.length();

		} while (norm > 1.0f);
		return result;
	}

	public static float normalize(float value, float min, float max) {
		float result = (value - min) / (max - min);
		return FastMath.clamp(result, 0.0f, 1.0f);
	}

	public static float normalizeAngle(float angle) {
		while (angle < 0.0f) {
			angle += FastMath.TWO_PI;
		}
		return angle % FastMath.TWO_PI;
	}

	public static float normalizeAngleDeg(float angle) {
		while (angle < 0.0f) {
			angle += 360.0f;
		}
		return angle % 360.0f;
	}

	public static float deltaAngle(float startAngle, float endAngle) {
		float leftDeltaAngle = (startAngle - endAngle + FastMath.TWO_PI) % FastMath.TWO_PI;
		float rightDeltaAngle = (endAngle - startAngle + FastMath.TWO_PI) % FastMath.TWO_PI;
		return leftDeltaAngle < rightDeltaAngle ? -leftDeltaAngle : rightDeltaAngle;
	}

	public static float deltaAngleDeg(float startAngle, float endAngle) {
		float leftDeltaAngle = (startAngle - endAngle + 360.0f) % 360.0f;
		float rightDeltaAngle = (endAngle - startAngle + 360.0f) % 360.0f;
		return leftDeltaAngle < rightDeltaAngle ? -leftDeltaAngle : rightDeltaAngle;
	}

	private static Vector3f angularVelocity(Quaternion orientation1, Quaternion orientation2, float time) {
		Quaternion quaternion = orientation2.mult(orientation1.inverse());
		Vector3f result = new Vector3f();
		float angle = quaternion.toAngleAxis(result);
		angle = deltaAngle(0.0f, angle);
		return result.multLocal(angle / time);
	}
}
