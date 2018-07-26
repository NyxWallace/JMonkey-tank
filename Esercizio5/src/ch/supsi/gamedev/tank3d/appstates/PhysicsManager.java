package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.controls.PhysicsListener;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Set;

public class PhysicsManager extends AbstractAppState implements PhysicsTickListener {

	private Node rootNode = null;

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		if (application instanceof SimpleApplication) {
			SimpleApplication simpleApplication = (SimpleApplication) application;
			rootNode = simpleApplication.getRootNode();
		}
                stateManager.attach(new BulletAppState());
		// --- Do stuff ---
	}

	@Override
	public void cleanup() {
		// --- Do stuff ---
	}

	@Override
	public void update(float tpf) {
		/*BulletAppState bulletAppState = stateManager.getState(BulletAppState.class);
                PhysicsSpace ps = bulletAppState.getPhysicsSpace();
                Set<RigidBodyControl> set = Utils.descendantsControls(rootNode, RigidBodyControl.class);
                for (RigidBodyControl rbc : set) {
                    ps.add(rbc);
                }*/
	}
	
	@Override
	public void prePhysicsTick(PhysicsSpace space, float tpf) {
                Set<Spatial> set = Utils.descendantsWithControl(rootNode, PhysicsListener.class);
                //System.out.println(set.size());
                for(Spatial s : set){
                    //System.out.println(s.getName());
                    //System.out.println("HERE");
                    PhysicsListener pl = s.getControl(PhysicsListener.class);
                    if(pl.isEnabled()){
                        pl.physicsUpdate(tpf);

                    }
                }
		// --- Do stuff ---
	}

	@Override
	public void physicsTick(PhysicsSpace space, float tpf) {
	}
}
