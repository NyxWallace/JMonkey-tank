package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.appstates.EventsManager;
import ch.supsi.gamedev.tank3d.appstates.LightsManager;
import ch.supsi.gamedev.tank3d.appstates.PhysicsManager;
import ch.supsi.gamedev.tank3d.appstates.ServerAppState;
import ch.supsi.gamedev.tank3d.appstates.servergameappstate.ServerGameAppState;
import ch.supsi.gamedev.tank3d.controls.multiplayercontrols.TankSpawnTxControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class ServerMain extends SimpleApplication {

	public static void main(String[] arguments) {
		ServerMain serverMain = new ServerMain();
		serverMain.setPauseOnLostFocus(false);
		//serverMain.start(JmeContext.Type.Headless);

		serverMain.start();
	}

	private void initScene() {
		Node scene = (Node) assetManager.loadModel("Scenes/Scene.j3o");
		rootNode.attachChild(scene);

		Spatial tankSpawn1 = scene.getChild("tankSpawn1");
		if (tankSpawn1 != null) {
			tankSpawn1.addControl(new TankSpawnTxControl());
		}

		Spatial tankSpawn2 = scene.getChild("tankSpawn2");
		if (tankSpawn2 != null) {
			tankSpawn2.addControl(new TankSpawnTxControl());
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

	private void initServer() {
		ServerAppState serverAppState = stateManager.getState(ServerAppState.class);
		if (serverAppState == null) {
			return;
		}
		serverAppState.host();
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

		stateManager.attach(new LightsManager());
		stateManager.attach(new BulletAppState());
		stateManager.attach(new PhysicsManager());
		stateManager.attach(new AudioManager());
		stateManager.attach(new EventsManager());
		stateManager.attach(new ServerAppState());
		stateManager.attach(new ServerGameAppState());

		initScene();
		initPhysics();
		initFilters();
		initServer();
	}

	@Override
	public void simpleUpdate(float tpf) {
		ServerGameAppState serverGameAppState = stateManager.getState(ServerGameAppState.class);
		if (!serverGameAppState.isGameStarted() && serverGameAppState.canStartGame()) {
			serverGameAppState.startGame();
		}
	}

	@Override
	public void simpleRender(RenderManager renderManager) {
	}
}
