package ch.supsi.gamedev.tank3d.utils;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.util.HashSet;
import java.util.Set;

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

	public static <T extends Control> Set<T> descendantsControls(Spatial spatial, final Class<T> controlClass) {
		final Set<T> results = new HashSet<>();
		
		SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				T control = spatial.getControl(controlClass);
				if (control != null) {
					results.add(control);
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
}
