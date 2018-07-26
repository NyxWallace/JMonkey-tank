package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.appstates.ClientAppState;
import ch.supsi.gamedev.tank3d.appstates.ClientGameAppState;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.appstates.InputsManager;
import ch.supsi.gamedev.tank3d.appstates.LightsManager;
import ch.supsi.gamedev.tank3d.appstates.PhysicsManager;
import ch.supsi.gamedev.tank3d.controls.multiplayercontrols.TankSpawnRxControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
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

public class ClientMain extends SimpleApplication {

	public static void main(String[] arguments) {
		ClientMain clientMain = new ClientMain();
		clientMain.setPauseOnLostFocus(false);
		clientMain.start();
	}

	private void initScene() {
		Node scene = (Node) assetManager.loadModel("Scenes/Scene.j3o");
		rootNode.attachChild(scene);

		Spatial tankSpawn1 = scene.getChild("tankSpawn1");
		if (tankSpawn1 != null) {
			tankSpawn1.addControl(new TankSpawnRxControl());
		}

		Spatial tankSpawn2 = scene.getChild("tankSpawn2");
		if (tankSpawn2 != null) {
			tankSpawn2.addControl(new TankSpawnRxControl());
		}

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
	}

	private void initClient() {
		ClientAppState clientAppState = stateManager.getState(ClientAppState.class);
		if (clientAppState == null) {
			return;
		}
		clientAppState.connect("10.11.210.146");
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(16.0f);

		Globals.SINGLETON.setAudioRenderer(audioRenderer);
		Globals.SINGLETON.setAssetManager(assetManager);
		Globals.SINGLETON.setStateManager(stateManager);
		Globals.SINGLETON.setInputManager(inputManager);
		Globals.SINGLETON.setCamera(cam);
		Globals.SINGLETON.setRootNode(rootNode);

		stateManager.attach(new InputsManager());
		stateManager.attach(new LightsManager());
		stateManager.attach(new BulletAppState());
		stateManager.attach(new PhysicsManager());
		stateManager.attach(new AudioManager());
		stateManager.attach(new EventsManager());
		stateManager.attach(new ClientAppState());
		stateManager.attach(new ClientGameAppState());

		initScene();
		initPhysics();
		initFilters();
		initInput();
		initClient();
	}
	private boolean first = true;

	@Override
	public void simpleUpdate(float tpf) {
		if (first) {
			flyCam.setEnabled(false);
			inputManager.setCursorVisible(false);
			first = false;
		}
		
		ClientGameAppState clientGameAppState = stateManager.getState(ClientGameAppState.class);
		if (!clientGameAppState.isPlayerReady() && clientGameAppState.canReadyPlayer()) {
			clientGameAppState.readyPlayer();
		}
	}

	@Override
	public void simpleRender(RenderManager renderManager) {
	}
}
