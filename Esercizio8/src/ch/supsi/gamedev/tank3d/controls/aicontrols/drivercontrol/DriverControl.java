package ch.supsi.gamedev.tank3d.controls.aicontrols.drivercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.controls.aicontrols.BehavioursManager;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.NavigationControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import ch.supsi.gamedev.tank3d.controls.listeners.EventListener;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

public class DriverControl extends AbstractControl implements EventListener, Cloneable {

	private final BehavioursManager behavioursManager = new BehavioursManager();
	
	// SceneGraph
	private HelmControl helmControl = null;
	private NavigationControl navigationControl = null;
	private WarningControl warningControl = null;
	private boolean initialized = false;

	private void initialize() {
		helmControl = spatial.getControl(HelmControl.class);
		if (helmControl == null) {
			return;
		}

		navigationControl = spatial.getControl(NavigationControl.class);
		if (navigationControl == null) {
			return;
		}

		warningControl = spatial.getControl(WarningControl.class);
		if (warningControl == null) {
			return;
		}
		
		Node rootNode = Globals.SINGLETON.getRootNode();
		List<Vector3f> waypoints = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Spatial waypointSpatial = Utils.getChild(rootNode, "waypoint" + (i + 1), Spatial.class);
			Vector3f waypoint = waypointSpatial != null ? waypointSpatial.getWorldTranslation() : null;
			if (waypoint != null) {
				waypoints.add(waypoint);
			}
		}
		behavioursManager.clear();
		behavioursManager.addBehaviour(new PatrolBehaviour(helmControl, warningControl, navigationControl, waypoints.toArray(new Vector3f[]{})));
		behavioursManager.addBehaviour(new EvadeBehaviour(helmControl, warningControl, navigationControl));
		behavioursManager.setCurrentBehaviour(PatrolBehaviour.class);
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		
		behavioursManager.update(tpf);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public BehavioursManager getBehavioursManager() {
		return behavioursManager;
	}

	@Override
	public void event(Event event) {
		behavioursManager.event(event);
	}
}
