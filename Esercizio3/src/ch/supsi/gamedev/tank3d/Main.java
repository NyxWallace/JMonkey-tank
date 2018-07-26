package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.spatialfactories.ProjectileFactory;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Main extends SimpleApplication implements ActionListener {

	public static void main(String[] arguments) {
		Main main = new Main();
		main.start();
	}

	private void initScene() {
		cam.setLocation(new Vector3f(-8.0f, 4.0f, 12.0f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
                //Spatial tank = assetManager.loadModel("Models/TankModel.j3o");
                Spatial scene = assetManager.loadModel("Scenes/Scene.j3o");
               
                Spatial nozzle = Utils.getChild((Node)scene, "nozzle", Node.class);
                SpawnControl sc = nozzle.getControl(SpawnControl.class);
                sc.setSpatialFactory(new ProjectileFactory(assetManager));

		//GridFactory gridFactory = new GridFactory(assetManager);
		//Node grid = gridFactory.newSpatial("grid");
		//rootNode.attachChild(grid);

		//TankFactory tankFactory = new TankFactory(assetManager);
		//Node tank = tankFactory.newSpatial("tank");
		rootNode.attachChild(scene);
	}

	private void initFilters() {
		BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Scene);
		bloomFilter.setBlurScale(2.0f);
		bloomFilter.setExposurePower(6.0f);
		bloomFilter.setExposureCutOff(0.0f);
		bloomFilter.setBloomIntensity(4.0f);
		FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
		filterPostProcessor.addFilter(bloomFilter);
                FilterPostProcessor fpp = assetManager.loadFilter("Filters/Filter.j3f");
                viewPort.addProcessor(fpp);
		viewPort.addProcessor(filterPostProcessor);
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

		// Listeners registration
		inputManager.addListener(this, "FlyCamEnable");
		Spatial tank = rootNode.getChild("tank");
		PlayerTankControl playerTankControl = tank != null ? tank.getControl(PlayerTankControl.class) : null;
		if (playerTankControl != null) {
			inputManager.addListener(playerTankControl, "Forward", "Backward", "Left", "Right", "TurretLeft", "TurretRight", "CannonUp", "CannonDown", "Fire");
		}
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(16.0f);

		initScene();
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
				if (playerTankControl != null) {
					playerTankControl.setEnabled(!isPressed);
				}
				inputManager.setCursorVisible(false);
				break;
		}
	}
}
