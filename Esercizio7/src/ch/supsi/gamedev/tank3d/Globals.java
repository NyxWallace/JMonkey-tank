package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.controls.CameraControl;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class Globals {
	
	public static final Globals SINGLETON = new Globals();
	
	private AssetManager assetManager = null;
	private AppStateManager stateManager = null;
	private Camera camera = null;
	private CameraControl cameraControl = null;
	private Node rootNode = null;

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public AppStateManager getStateManager() {
		return stateManager;
	}

	public void setStateManager(AppStateManager stateManager) {
		this.stateManager = stateManager;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public CameraControl getCameraControl() {
		return cameraControl;
	}

	public void setCameraControl(CameraControl cameraControl) {
		this.cameraControl = cameraControl;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
}
