package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.appstates.LightsManager;
import ch.supsi.gamedev.tank3d.appstates.PhysicsManager;
import ch.supsi.gamedev.tank3d.controls.CameraControl;
import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.PhysicsTankControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
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

		// Shadows
		LightList lights = scene.getLocalLightList();
		Light light = lights.size() > 0 ? lights.get(0) : null;
		if (light instanceof DirectionalLight) {
			DirectionalLight directionalLight = (DirectionalLight) light;
			DirectionalLightShadowRenderer directionalLightShadowRenderer = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
			DirectionalLightShadowFilter directionalLightShadowFilter = new DirectionalLightShadowFilter(assetManager, 1024, 3);
			directionalLightShadowRenderer.setLight(directionalLight);
			directionalLightShadowFilter.setLight(directionalLight);
			FilterPostProcessor shadowPostProcessor = new FilterPostProcessor(assetManager);
			shadowPostProcessor.addFilter(directionalLightShadowFilter);
			viewPort.addProcessor(directionalLightShadowRenderer);
			viewPort.addProcessor(shadowPostProcessor);
		}
	}

	private void initPhysics() {
		BulletAppState bulletAppState = stateManager.getState(BulletAppState.class);
		if (bulletAppState == null) {
			return;
		}
		bulletAppState.getPhysicsSpace().setAccuracy(1.0f / 60.0f);
		bulletAppState.getPhysicsSpace().getDynamicsWorld().getSolverInfo().numIterations = 16;
	}

	private void initFilters() {
		FilterPostProcessor filter = assetManager.loadFilter("Filters/Filter.j3f");
		viewPort.addProcessor(filter);
	}

	private void initInput() {
		// Input mapping
		inputManager.addMapping("FlyCamEnable", new MouseButtonTrigger(1));
		inputManager.addMapping("PhysicsDebugToggle", new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping("DriverCamera", new KeyTrigger(KeyInput.KEY_1));
		inputManager.addMapping("GunnerCamera", new KeyTrigger(KeyInput.KEY_2));
		inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("StrafeLeft", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("StrafeRight", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("TurretLeft", new MouseAxisTrigger(0, true));
		inputManager.addMapping("TurretRight", new MouseAxisTrigger(0, false));
		inputManager.addMapping("CannonUp", new MouseAxisTrigger(1, false));
		inputManager.addMapping("CannonDown", new MouseAxisTrigger(1, true));
		inputManager.addMapping("Fire", new MouseButtonTrigger(0));

		// Listeners registration
		inputManager.addListener(this, "FlyCamEnable", "PhysicsDebugToggle");
		Spatial player = rootNode.getChild("player");
		PlayerTankControl playerTankControl = player != null ? player.getControl(PlayerTankControl.class) : null;
		if (playerTankControl != null) {
			inputManager.addListener(playerTankControl, "DriverCamera", "GunnerCamera", "Forward", "Backward", "StrafeLeft", "StrafeRight", "Left", "Right", "TurretLeft", "TurretRight", "CannonUp", "CannonDown", "Fire");
		}
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(16.0f);

		stateManager.attach(new LightsManager());
		stateManager.attach(new BulletAppState());
		stateManager.attach(new PhysicsManager());
		stateManager.attach(new EventsManager());
                stateManager.attach(new AudioManager());

		// Globals
		Globals.SINGLETON.setAssetManager(assetManager);
		Globals.SINGLETON.setStateManager(stateManager);
		Globals.SINGLETON.setCamera(cam);
		Globals.SINGLETON.setRootNode(rootNode);

		initScene();
		initPhysics();
		initFilters();
		initInput();
	}
	private boolean first = true;

	@Override
	public void simpleUpdate(float tpf) {
		if (first) {
			flyCam.setEnabled(false);
			inputManager.setCursorVisible(false);
			first = false;
		}
		Spatial enemy = rootNode.getChild("enemy");
		PhysicsTankControl physicsTankControl = enemy != null ? enemy.getControl(PhysicsTankControl.class) : null;
		if (physicsTankControl != null) {
			physicsTankControl.rotateTurret(0.5f * tpf);
			physicsTankControl.rotateCannon(0.5f * tpf);
		}
	}

	@Override
	public void simpleRender(RenderManager renderManager) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		Spatial tank = rootNode.getChild("player");
		Node music = Utils.getChild(rootNode, "music", Node.class);
		PlayerTankControl playerTankControl = tank != null ? tank.getControl(PlayerTankControl.class) : null;
		switch (name) {
			case "FlyCamEnable":
				flyCam.setEnabled(isPressed);
				CameraControl cameraControl = Globals.SINGLETON.getCameraControl();
				if (cameraControl != null) {
					cameraControl.setEnabled(!isPressed);
				}
				if (playerTankControl != null) {
					playerTankControl.setEnabled(!isPressed);
				}
				inputManager.setCursorVisible(false);
				break;
			case "PhysicsDebugToggle":
				BulletAppState bulletAppState = stateManager.getState(BulletAppState.class);
				if (bulletAppState != null && isPressed) {
					boolean debugEnabled = bulletAppState.isDebugEnabled();
					bulletAppState.setDebugEnabled(!debugEnabled);
				}
				break;
		}
	}
}
