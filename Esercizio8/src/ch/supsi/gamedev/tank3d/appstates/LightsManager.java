package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.controls.LightControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LightsManager extends AbstractAppState {

	private static final float DEFAULT_UPDATE_TIME = 0.3f;
	private final Map<LightControl, Collection<Light>> map = new HashMap<>();
	private Node rootNode = null;
	private float updateTime = DEFAULT_UPDATE_TIME;
	private float elapsedTime = 0.0f;

	public float getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(float updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		if (application instanceof SimpleApplication) {
			SimpleApplication simpleApplication = (SimpleApplication) application;
			rootNode = simpleApplication.getRootNode();
		}
	}

	@Override
	public void update(float tpf) {
		if (rootNode == null) {
			return;
		}
		elapsedTime += tpf;
		if (elapsedTime < updateTime) {
			return;
		}
		elapsedTime -= updateTime;
		Set<LightControl> lightControls = Utils.descendantsControls(rootNode, LightControl.class);
		Set<LightControl> addedLightControls = new HashSet<>(lightControls);
		addedLightControls.removeAll(map.keySet());
		Set<LightControl> removedLightControls = new HashSet<>(map.keySet());
		removedLightControls.removeAll(lightControls);
		for (LightControl addedLightControl : addedLightControls) {
			Collection<Light> lights = addedLightControl.lights();
			for (Light light : lights) {
				rootNode.addLight(light);
			}
			map.put(addedLightControl, addedLightControl.lights());
		}
		for (LightControl removedLightControl : removedLightControls) {
			Collection<Light> lights = map.remove(removedLightControl);
			if (lights != null) {
				for (Light light : lights) {
					rootNode.removeLight(light);
				}
			}
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		for (Collection<Light> lights : map.values()) {
			for (Light light : lights) {
				rootNode.removeLight(light);
			}
		}
		map.clear();
	}
}
