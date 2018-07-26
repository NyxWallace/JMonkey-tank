package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NavigationControl extends AbstractControl implements Cloneable {

	private static final float DEFAULT_RADIUS = 16.0f;
	private final List<Vector2f> waypoints = new ArrayList<>();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private Vector2f currentWaypoint = null;
	private int currentWaypointIndex = 0;
	private NavMeshPathfinder pathfinder = null;
	private float radius = DEFAULT_RADIUS;
	private boolean initialized = false;

	private Vector2f waypoint(Vector3f position) {
		return new Vector2f(position.getZ(), -position.getX());
	}

	private Vector2f spatialPosition() {
		Vector3f spatialPosition = spatial.getWorldTranslation();
		return waypoint(spatialPosition);
	}

	private Vector2f getPreviousWaypoint() {
		if (currentWaypoint == null) {
			return null;
		}
		return currentWaypointIndex > 0 ? waypoints.get(currentWaypointIndex - 1) : null;
	}

	private Vector2f getNextWaypoint() {
		if (currentWaypoint == null) {
			return null;
		}
		return currentWaypointIndex < waypoints.size() - 1 ? waypoints.get(currentWaypointIndex + 1) : null;
	}
	
	private boolean hasReached() {
		if (currentWaypoint == null) {
			return true;
		}
		Vector2f spatialPosition = spatialPosition();
		return currentWaypoint.subtract(spatialPosition).lengthSquared() < radius * radius;
	}

	private void initialize() {
		Node rootNode = Globals.SINGLETON.getRootNode();
		Node terrain = Utils.getChild(rootNode, "terrain", Node.class);
		Geometry navMeshGeometry = Utils.getChild(terrain, "navMeshGeometry", Geometry.class);
		if (navMeshGeometry == null) {
			return;
		}
		NavMesh navMesh = new NavMesh(navMeshGeometry.getMesh());
		pathfinder = new NavMeshPathfinder(navMesh);
		pathfinder.setEntityRadius(radius);
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}

		if (currentWaypoint == null) {
			return;
		}
		if (hasReached()) {
			currentWaypoint = getNextWaypoint();
			currentWaypointIndex++;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public boolean plotCourse(final Vector3f goal) {
		if (pathfinder == null) {
			return false;
		}
		pathfinder.setPosition(spatial.getWorldTranslation());
		if (!pathfinder.computePath(goal)) {
			return false;
		}
		ArrayList<Path.Waypoint> pathWaypoints = pathfinder.getPath().getWaypoints();
		waypoints.clear();
		for (int index = 1; index < pathWaypoints.size() - 1; index++) {
			Vector3f pathWaypointPosition = pathWaypoints.get(index).getPosition();
			Vector2f waypoint = waypoint(pathWaypointPosition);
			waypoints.add(waypoint);
		}
		currentWaypointIndex = 0;
		currentWaypoint = waypoints.isEmpty() ? null : waypoints.get(0);
		return currentWaypoint == null;
	}

	public int getWaypointsCount() {
		return waypoints.size();
	}

	public Vector2f getVector() {
		Vector2f spatialPosition = spatialPosition();
		Vector2f waypointPosition = currentWaypoint;
		return waypointPosition.subtract(spatialPosition);
	}

	public Vector2f getApproachVector() {
		Vector2f previousWaypoint = getPreviousWaypoint();
		if (previousWaypoint == null) {
			return null;
		}
		return currentWaypoint.subtract(previousWaypoint);
	}

	public float getAzimuth() {
		Vector2f vector = getVector();
		return vector != null ? vector.getAngle() * FastMath.RAD_TO_DEG : 0.0f;
	}

	public float getApproachAzimuth() {
		Vector2f vector = getVector();
		Vector2f approachVector = getApproachVector();
		float azimuth = getAzimuth();
		if (vector == null || approachVector == null) {
			return azimuth;
		}
		Vector2f approachVersor = approachVector.normalize();
		Vector2f approachNormal = new Vector2f(-approachVersor.getY(), approachVersor.getX());
		float distance = vector.dot(approachVersor);
		float error = vector.dot(approachNormal);
		if (distance <= 0.0f) {
			return azimuth;
		}
		float factor = error / distance;
		float result = azimuth + factor * 90.0f;
		return result;
	}

	public float getDistance() {
		Vector2f vector = getVector();
		return vector != null ? vector.length() : 0.0f;
	}

	public float getError() {
		Vector2f waypointVector = getVector();
		Vector2f waypointApproachVector = getApproachVector();
		if (waypointVector == null || waypointApproachVector == null) {
			return 0.0f;
		}
		Vector2f waypointApproachVersor = waypointApproachVector.normalize();
		Vector2f waypointApproachNormal = new Vector2f(-waypointApproachVersor.getY(), -waypointApproachVersor.getX());
		return waypointVector.dot(waypointApproachNormal);
	}

	public boolean isFirstWaypoint() {
		return currentWaypointIndex == 0;
	}

	public boolean isLastWaypoint() {
		return currentWaypointIndex == waypoints.size() - 1;
	}

	public boolean onCourse() {
		return currentWaypoint != null;
	}
}
