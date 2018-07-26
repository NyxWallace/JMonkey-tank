package ch.supsi.gamedev.tank3d.controls;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.Collection;

public class LightControl<ThisLight extends Light> extends AbstractControl implements Cloneable {
	
	// SceneGraph
	private Collection<Light> lights = null;

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		LightList lightList = spatial.getLocalLightList();
		lights = new ArrayList<>();
		for (Light light : lightList) {
			lights.add(light.clone());
		}
	}

	@Override
	protected void controlUpdate(float tpf) {
		Vector3f worldTranslation = spatial.getWorldTranslation();
		Quaternion worldRotation = spatial.getWorldRotation();
		Vector3f aheadVector = worldRotation.getRotationColumn(2);
		for (Light light : lights) {
			switch (light.getType()) {
				case Directional:
					DirectionalLight directionalLight = (DirectionalLight) light;
					directionalLight.setDirection(aheadVector);
					break;
				case Point:
					PointLight pointLight = (PointLight) light;
					pointLight.setPosition(worldTranslation);
					break;
				case Spot:
					SpotLight spotLight = (SpotLight) light;
					spotLight.setPosition(worldTranslation);
					spotLight.setDirection(aheadVector);
					break;
			}
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
	
	public Collection<Light> lights() {
		return lights;
	}
}
