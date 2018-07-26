/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.effects.EmitterCircleShape;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Antoine
 */
public class DustControl extends AbstractControl implements Cloneable {
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if(spatial instanceof ParticleEmitter){
            ParticleEmitter particleEmitter = (ParticleEmitter) spatial;
            particleEmitter.setShape(new EmitterCircleShape(3.0f));
        }
            
    }
    
    @Override
    protected void controlUpdate(float tpf) {}
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
