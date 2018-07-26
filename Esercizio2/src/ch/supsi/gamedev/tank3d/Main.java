package ch.supsi.gamedev.tank3d;

import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.spatialfactories.GridFactory;
import ch.supsi.gamedev.tank3d.spatialfactories.TankFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Main extends SimpleApplication implements ActionListener {

    Node tank;

    public static void main(String[] arguments) {
        Main main = new Main();
        main.start();
    }

    private void initScene() {
        cam.setLocation(new Vector3f(-8.0f, 4.0f, 12.0f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        GridFactory gridFactory = new GridFactory(assetManager);
        Node grid = gridFactory.newSpatial("grid");
        rootNode.attachChild(grid);

        TankFactory tankFactory = new TankFactory(assetManager);
        tank = tankFactory.newSpatial("tank");
        rootNode.attachChild(tank);
    }

    private void initFilters() {
        BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloomFilter.setBlurScale(2.0f);
        bloomFilter.setExposurePower(6.0f);
        bloomFilter.setExposureCutOff(0.0f);
        bloomFilter.setBloomIntensity(4.0f);
        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        filterPostProcessor.addFilter(bloomFilter);
        viewPort.addProcessor(filterPostProcessor);
    }

    private void initInput() {
        // Input mapping
        inputManager.addMapping("EnableFlyCam", new MouseButtonTrigger(1)); // Eanble flyCam with right mouse button

        inputManager.addMapping("Forward", new KeyTrigger(keyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(keyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(keyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("TurretLeft", new KeyTrigger(keyInput.KEY_Q));
        inputManager.addMapping("TurretRight", new KeyTrigger(keyInput.KEY_E));
        inputManager.addMapping("CannonUp", new KeyTrigger(keyInput.KEY_R));
        inputManager.addMapping("CannonDown", new KeyTrigger(keyInput.KEY_F));
        inputManager.addMapping("Fire", new KeyTrigger(keyInput.KEY_SPACE));

        // Listeners registration
        inputManager.addListener(this, "EnableFlyCam");

        inputManager.addListener(tank.getControl(PlayerTankControl.class), "Forward", "Backward", "Left", "Right","TurretLeft","TurretRight","CannonUp","CannonDown","Fire");

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
        switch (name) {
            case "EnableFlyCam":
                flyCam.setEnabled(isPressed);
                inputManager.setCursorVisible(false);
                break;
        }
    }
}
