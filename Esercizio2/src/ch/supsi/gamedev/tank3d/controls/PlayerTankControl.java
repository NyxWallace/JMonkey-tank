/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.controls;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Antoine
 */
public class PlayerTankControl extends AbstractControl implements ActionListener, AnalogListener {

    private boolean forward = false;
    private boolean backward = false;
    private boolean right = false;
    private boolean left = false;
    private boolean turretLeft = false;
    private boolean turretRight = false;
    private boolean cannonUp = false;
    private boolean cannonDown = false;
    private boolean fire = false;
    
    private KinematicTankControl kinematicTankControl = null;


    @Override
    protected void controlUpdate(float tpf) {
        if (forward)
            kinematicTankControl.setThrottle(1.0f);
        else if (backward)
            kinematicTankControl.setThrottle(-1);
        else 
            kinematicTankControl.setThrottle(0);
        
        if(right)
            kinematicTankControl.setSteering(-0.2f);
        else if(left)
            kinematicTankControl.setSteering(0.2f);
        else
            kinematicTankControl.setSteering(0);
        
        if(turretRight)
            kinematicTankControl.rotateTurret(-0.2f);
        else if(turretLeft)
            kinematicTankControl.rotateTurret(0.2f);
        else
            kinematicTankControl.rotateTurret(0);
        
        if(cannonUp)
            kinematicTankControl.rotateCannon(-0.2f);
        else if(cannonDown)
            kinematicTankControl.rotateCannon(0.2f);
        else
            kinematicTankControl.rotateCannon(0);
        
        if(fire)
            kinematicTankControl.fire();
        
        
        
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Forward":
                forward = isPressed;
                break;
            case "Backward":
                backward = isPressed;
                break;
            case "Right":
                right = isPressed;
                break;
            case "Left":
                left = isPressed;
                break;
            case "TurretLeft":
                turretLeft = isPressed;
                break;
            case "TurretRight":
                turretRight = isPressed;
                break;
            case "CannonUp":
                cannonUp = isPressed;
                break;
            case "CannonDown":
                cannonDown = isPressed;
                break;
            case "Fire":
                fire = isPressed;
                break;
            default:
                break;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        kinematicTankControl = spatial.getControl(KinematicTankControl.class);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
