package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.controls.CollisionListener;
import ch.supsi.gamedev.tank3d.controls.PhysicsListener;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.Set;

public class PhysicsManager extends AbstractAppState implements PhysicsTickListener, PhysicsCollisionListener{

	private Node rootNode = null;
	private BulletAppState bulletAppState = null;
	private Set<Spatial> spatials = new HashSet<>();

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		if (application instanceof SimpleApplication) {
			SimpleApplication simpleApplication = (SimpleApplication) application;
			rootNode = simpleApplication.getRootNode();
		}
		bulletAppState = stateManager.getState(BulletAppState.class);
		if (bulletAppState != null) {
			bulletAppState.getPhysicsSpace().addTickListener(this);
                        bulletAppState.getPhysicsSpace().addCollisionListener(this);                    
		}
	}

	@Override
	public void cleanup() {
		for (Spatial spatial : spatials) {
			bulletAppState.getPhysicsSpace().remove(spatial);
		}
		spatials.clear();
		if (bulletAppState != null) {
			bulletAppState.getPhysicsSpace().removeTickListener(this);
		}
	}

	@Override
	public void update(float tpf) {
		Set<Spatial> newSpatials = Utils.descendantsWithControl(rootNode, RigidBodyControl.class);
		Set<Spatial> addedSpatials = new HashSet<>(newSpatials);
		addedSpatials.removeAll(spatials);
		Set<Spatial> removedSpatials = new HashSet<>(spatials);
		removedSpatials.removeAll(newSpatials);
		spatials = newSpatials;
		for (Spatial addedSpatial : addedSpatials) {
			bulletAppState.getPhysicsSpace().add(addedSpatial);
			RigidBodyControl rigidBodyControl = addedSpatial.getControl(RigidBodyControl.class);
			rigidBodyControl.setPhysicsLocation(addedSpatial.getWorldTranslation());
			rigidBodyControl.setPhysicsRotation(addedSpatial.getWorldRotation());
		}
		for (Spatial removedSpatial : removedSpatials) {
			bulletAppState.getPhysicsSpace().remove(removedSpatial);
		}
	}
	
	@Override
	public void prePhysicsTick(PhysicsSpace space, float tpf) {
		Set<PhysicsListener> physicsListeners = Utils.descendantsControls(rootNode, PhysicsListener.class);
                Set<CollisionListener>collisionListeners = Utils.descendantsControls(rootNode, CollisionListener.class);
		for (PhysicsListener physicsListener : physicsListeners) {
			if (physicsListener.isEnabled()) {
				physicsListener.physicsUpdate(tpf);
			}
		}
                
                for(CollisionListener cl : collisionListeners){
                    if(cl.isEnabled()){
                        
                    }
                }
	}

	@Override
	public void physicsTick(PhysicsSpace space, float tpf) {
	}

    @Override
    public void collision(PhysicsCollisionEvent event) {
        Set<Spatial> collisionListeners = Utils.descendantsWithControl(rootNode, CollisionListener.class);
        Spatial spatialA = event.getNodeA();
        Spatial spatialB = event.getNodeB();
        
        if(collisionListeners.contains(spatialA)){
            CollisionListener cl = spatialA.getControl(CollisionListener.class);
            if(cl.isEnabled()){
                cl.collision(spatialB, event.getLocalPointB(), event.getNormalWorldOnB());
            }
        }
        if(collisionListeners.contains(spatialB)){
            CollisionListener cl = spatialB.getControl(CollisionListener.class);
            if(cl.isEnabled()){
                cl.collision(spatialA, event.getLocalPointA(), event.getNormalWorldOnB());
            }
        }
    }
}
