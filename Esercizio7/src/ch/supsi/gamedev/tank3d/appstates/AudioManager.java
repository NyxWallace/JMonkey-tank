/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antoine
 */

public class AudioManager extends AbstractAppState{
    private Camera camera;
    private Listener listener;
    private Vector3f position;
    private List<AudioNode> audioNodes = new ArrayList<AudioNode>();

    @Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
                camera = application.getCamera();
                listener = application.getListener();
                position = camera.getLocation();
	}
    
	@Override
	public void update(float tpf) {
            listener.setLocation(camera.getLocation());
            listener.setRotation(camera.getRotation());
            listener.setVelocity(camera.getLocation().subtract(position).divide(tpf));
            position = camera.getLocation();
            
            
	}
        
        public void AddAudioNode(AudioNode node){
            audioNodes.add(node);
        }
}
