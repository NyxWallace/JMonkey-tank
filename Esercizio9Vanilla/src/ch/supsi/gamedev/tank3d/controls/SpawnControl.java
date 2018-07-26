package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.Globals;
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

	// Properties
	private String model = null;

	@Override
	protected void controlUpdate(float tpf) {
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
		AssetManager assetManager = Globals.SINGLETON.getAssetManager();
		Spatial result = assetManager != null ? assetManager.loadModel(model) : null;
		if (result == null) {
			return null;
		}
		result.setName(name);

		Node rootNode = Globals.SINGLETON.getRootNode();
		Node scene = Utils.getChild(rootNode, "scene", Node.class);
		if (scene == null) {
			return null;
		}
		scene.attachChild(result);
		Transform transform = spatial.getWorldTransform();
		result.setLocalTransform(transform);
		return result;
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
