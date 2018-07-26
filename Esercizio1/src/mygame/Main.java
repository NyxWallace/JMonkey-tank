package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    float angle = 0.0f;
    float angleTurret = 0.0f;
    float raise = 0.0f;
    float speed_raise = 0.6f;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(16.0f);
        
        TankFactory tankfactory = new TankFactory(assetManager);
        GridFactory gridfactory = new GridFactory(assetManager);
        
        Node tank1 = tankfactory.newTank("Alpha");
        tank1.setLocalTranslation(0,0.5f,0);
        tank1.setLocalRotation(new Quaternion(new float[]{0.0f, FastMath.PI/2.0f, 0.0f}));
        
        rootNode.attachChild(gridfactory.newGrid("Plan"));
        rootNode.attachChild(tank1);
        
        BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloomFilter.setBlurScale(1.5f);
        bloomFilter.setExposurePower(5.0f);
        bloomFilter.setExposureCutOff(0.0f);
        bloomFilter.setBloomIntensity(2.0f);
        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        filterPostProcessor.addFilter(bloomFilter);
        viewPort.addProcessor(filterPostProcessor);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Node tank1 = Utils.getChild(rootNode, "Alpha", Node.class);
        if(tank1 == null){
            return;
        }
        angle += tpf * 1.0f;
        angle %= FastMath.TWO_PI;
        tank1.setLocalRotation(new Quaternion(new float[]{0.0f, angle, 0.0f}));
        
        Node tank1turret = Utils.getChild(rootNode, "AlphaTurret", Node.class);
        if(tank1turret == null){
            return;
        }
        angleTurret += tpf * 3.0f;
        angleTurret %= FastMath.TWO_PI;
        tank1turret.setLocalRotation(new Quaternion(new float[]{0.0f, -angleTurret, 0.0f}));
        
        Node tank1cannon = Utils.getChild(rootNode, "AlphaCannon", Node.class);
        if(tank1cannon == null){
            return;
        }
        raise += tpf * speed_raise;
        if(raise < -FastMath.PI/4.0f || raise > FastMath.DEG_TO_RAD*(10))
            speed_raise*=(-1);
        tank1cannon.setLocalRotation(new Quaternion(new float[]{raise, 0.0f, 0.0f}));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
