package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class SpawnControl extends AbstractControl implements Cloneable {
	
	// Global
	private static AssetManager globalAssetManager = null;
	private static Node globalSpawnNode = null;

	public static AssetManager getGlobalAssetManager() {
		return globalAssetManager;
	}

	public static void setGlobalAssetManager(AssetManager globalAssetManager) {
		SpawnControl.globalAssetManager = globalAssetManager;
	}

	public static Node getGlobalSpawnNode() {
		return globalSpawnNode;
	}

	public static void setGlobalSpawnNode(Node globalSpawnNode) {
		SpawnControl.globalSpawnNode = globalSpawnNode;
	}
	// Properties
	private String model = null;
	// Transient
	private Spatial spawn = null;

	@Override
	protected void controlUpdate(float tpf) {
		Node spawnNode = globalSpawnNode != null ? globalSpawnNode : Utils.getRootNode(spatial);
		if (spawnNode == null || model == null) {
			return;
		}
		if (spawn != null) {
			spawnNode.attachChild(spawn);
			Transform transform = spatial.getWorldTransform();
			spawn.setLocalTransform(transform);
			spawn = null;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Spatial spawn(String name) {
		if (model == null) {
			return null;
		}
		spawn = globalAssetManager.loadModel(model);
		spawn.setName(name);
		return spawn;
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		model = capsule.readString("model", null);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(model, "model", null);
	}
}
