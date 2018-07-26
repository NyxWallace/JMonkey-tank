package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.SpatialFactory;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class SpawnControl extends AbstractControl implements Cloneable {

	// Properties
	private SpatialFactory spatialFactory = null;

	// Transient
	private boolean spawning = false;
	private String spawnName = null;

	@Override
	protected void controlUpdate(float tpf) {
		Node rootNode = Utils.getRootNode(spatial);
		if (rootNode == null || spatialFactory == null) {
			return;
		}
		if (spawning) {
			Spatial spawnedSpatial = spatialFactory.newSpatial(spawnName);
			rootNode.attachChild(spawnedSpatial);
			Transform transform = spatial.getWorldTransform();
			spawnedSpatial.setLocalTransform(transform);
			spawning = false;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
	public SpatialFactory getSpatialFactory() {
		return spatialFactory;
	}

	public void setSpatialFactory(SpatialFactory spatialFactory) {
		this.spatialFactory = spatialFactory;
	}

	public void spawn(String name) {
		if (spatialFactory == null || spatial == null) {
			return;
		}
		spawning = true;
		spawnName = name;
	}
}
