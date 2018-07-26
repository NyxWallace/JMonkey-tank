package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.appstates.LightsManager;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.AltimeterControl;
import ch.supsi.gamedev.tank3d.controls.CameraControl;
import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.effectcontrols.LaserControl;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.RangeControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class Main extends SimpleApplication implements ActionListener {

	public static void main(String[] arguments) {
		Main main = new Main();
		main.start();
	}

	private void initScene() {
		cam.setLocation(new Vector3f(-8.0f, 4.0f, 12.0f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		Node scene = (Node) assetManager.loadModel("Scenes/Scene.j3o");
		rootNode.attachChild(scene);

		// Control globals
		SpawnControl.setGlobalAssetManager(assetManager);
		SpawnControl.setGlobalSpawnNode(scene);
		Node terrain = Utils.getChild(scene, "terrain", Node.class);
		AltimeterControl.setGlobalTerrain(terrain);
		RangeControl.setRootNode(scene);
		CameraControl.setGlobalCamera(cam);
		LaserControl.setGlobalCamera(cam);
                
                terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
                
                DirectionalLight light = (DirectionalLight)scene.getLocalLightList().get(0);
                
                DirectionalLightShadowRenderer shadowRender = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
                DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 1024, 3);
                
                shadowRender.setLight(light);
                shadowFilter.setLight(light);
                
                FilterPostProcessor shadowPostProcessor = new FilterPostProcessor(assetManager);
                shadowPostProcessor.addFilter(shadowFilter);
                
                viewPort.addProcessor(shadowRender);
                viewPort.addProcessor(shadowPostProcessor);
	}

	private void initFilters() {
		FilterPostProcessor filter = assetManager.loadFilter("Filters/Filter.j3f");
		viewPort.addProcessor(filter);
	}

	private void initInput() {

		// Input mapping
		inputManager.addMapping("FlyCamEnable", new MouseButtonTrigger(1));
		inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("TurretLeft", new MouseAxisTrigger(0, true));
		inputManager.addMapping("TurretRight", new MouseAxisTrigger(0, false));
		inputManager.addMapping("CannonUp", new MouseAxisTrigger(1, false));
		inputManager.addMapping("CannonDown", new MouseAxisTrigger(1, true));
		inputManager.addMapping("Fire", new MouseButtonTrigger(0));
                inputManager.addMapping("DriverCam", new KeyTrigger(KeyInput.KEY_1));
                inputManager.addMapping("GunnerCam", new KeyTrigger(KeyInput.KEY_2));

		// Listeners registration
		inputManager.addListener(this, "FlyCamEnable");
		Spatial tank = rootNode.getChild("tank");
                
                tank.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                
		PlayerTankControl playerTankControl = tank != null ? tank.getControl(PlayerTankControl.class) : null;
		if (playerTankControl != null) {
			inputManager.addListener(playerTankControl, "Forward", "Backward", "Left", "Right", "TurretLeft", "TurretRight", "CannonUp", "CannonDown", "Fire", "DriverCam", "GunnerCam");
		}
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(16.0f);

		initScene();
		initFilters();
		initInput();

		stateManager.attach(new LightsManager());
	}
	private boolean first = true;

	@Override
	public void simpleUpdate(float tpf) {
		if (first) {
			flyCam.setEnabled(false);
			inputManager.setCursorVisible(false);
			first = false;
		}
	}

	@Override
	public void simpleRender(RenderManager renderManager) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		Spatial tank = rootNode.getChild("tank");
		PlayerTankControl playerTankControl = tank != null ? tank.getControl(PlayerTankControl.class) : null;
		switch (name) {
			case "FlyCamEnable":
				flyCam.setEnabled(isPressed);
				CameraControl globalCameraControl = CameraControl.getGlobalCameraControl();
				if (globalCameraControl != null) {
					globalCameraControl.setEnabled(!isPressed);
				}
				if (playerTankControl != null) {
					playerTankControl.setEnabled(!isPressed);
				}
				inputManager.setCursorVisible(false);
				break;
		}
	}
}
