package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.appstates.LightsManager;
import ch.supsi.gamedev.tank3d.appstates.PhysicsManager;
import ch.supsi.gamedev.tank3d.controls.CameraControl;
import ch.supsi.gamedev.tank3d.controls.MusicControl;
import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.NavigationControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.drivercontrol.DriverControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.gunnercontrol.GunnerControl;
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
		inputManager.addMapping("PlayTrack1", new KeyTrigger(KeyInput.KEY_3));
		inputManager.addMapping("PlayTrack2", new KeyTrigger(KeyInput.KEY_4));
		inputManager.addMapping("DriverCamera", new KeyTrigger(KeyInput.KEY_1));
		inputManager.addMapping("GunnerCamera", new KeyTrigger(KeyInput.KEY_2));

		inputManager.addMapping("Aux.0", new KeyTrigger(KeyInput.KEY_NUMPAD0));
		inputManager.addMapping("Aux.1", new KeyTrigger(KeyInput.KEY_NUMPAD1));
		inputManager.addMapping("Aux.2", new KeyTrigger(KeyInput.KEY_NUMPAD2));
		inputManager.addMapping("Aux.3", new KeyTrigger(KeyInput.KEY_NUMPAD3));
		inputManager.addMapping("Aux.4", new KeyTrigger(KeyInput.KEY_NUMPAD4));
		inputManager.addMapping("Aux.5", new KeyTrigger(KeyInput.KEY_NUMPAD5));
		inputManager.addMapping("Aux.6", new KeyTrigger(KeyInput.KEY_NUMPAD6));
		inputManager.addMapping("Aux.7", new KeyTrigger(KeyInput.KEY_NUMPAD7));
		inputManager.addMapping("Aux.8", new KeyTrigger(KeyInput.KEY_NUMPAD8));
		inputManager.addMapping("Aux.9", new KeyTrigger(KeyInput.KEY_NUMPAD9));

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
		inputManager.addListener(this, "FlyCamEnable", "PhysicsDebugToggle", "PlayTrack1", "PlayTrack2");
		inputManager.addListener(this, "Aux.0", "Aux.1", "Aux.2", "Aux.3", "Aux.4", "Aux.5", "Aux.6", "Aux.7", "Aux.8", "Aux.9");
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
		stateManager.attach(new AudioManager());
		stateManager.attach(new EventsManager());

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

			Spatial player = rootNode.getChild("player");
			Node playerTank = player instanceof Node ? (Node) player : null;
			Node turret = Utils.getChild(playerTank, "turret", Node.class);
			Node gunnerCamera = Utils.getChild(playerTank, "gunnerCamera", Node.class);
			CameraControl cameraControl = gunnerCamera != null ? gunnerCamera.getControl(CameraControl.class) : null;
			if (cameraControl != null) {
				cameraControl.setEnabled(true);
			}
			first = false;
		}
	}

	@Override
	public void simpleRender(RenderManager renderManager) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		Spatial player = rootNode.getChild("player");
		Spatial enemy = rootNode.getChild("enemy");
		Node music = Utils.getChild(rootNode, "music", Node.class);
		PlayerTankControl playerTankControl = player != null ? player.getControl(PlayerTankControl.class) : null;
		MusicControl musicControl = music != null ? music.getControl(MusicControl.class) : null;
		HelmControl helmControl = enemy != null ? enemy.getControl(HelmControl.class) : null;
		GunnerControl gunnerControl = enemy != null ? enemy.getControl(GunnerControl.class) : null;
		NavigationControl navigationControl = enemy != null ? enemy.getControl(NavigationControl.class) : null;
		DriverControl driverControl = enemy != null ? enemy.getControl(DriverControl.class) : null;
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
			case "PlayTrack1":
				if (musicControl != null && isPressed) {
					musicControl.play(0);
				}
				break;
			case "PlayTrack2":
				if (musicControl != null && isPressed) {
					musicControl.play(1);
				}
				break;
			case "Aux.0":
				break;
			case "Aux.1":
				break;
			case "Aux.2":
				break;
			case "Aux.3":
				break;
			case "Aux.4":
				break;
			case "Aux.5":
				break;
			case "Aux.6":
				break;
			case "Aux.7":
				break;
			case "Aux.8":
				break;
			case "Aux.9":
				break;
		}
	}
}
