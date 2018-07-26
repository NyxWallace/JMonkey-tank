/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Antoine
 */
public class KinematicTankControl extends AbstractControl implements TankControl {

    private float forwardSpeed = 20;
    private float backwardSpeed = 20;
    private float steeringSpeed = 20;
    private float turretSpeed = 20;
    private float cannonSpeed = 20;
    private float reloadTime = 5;
    
    private float throttle = 0.0f;
    private float steering = 0.0f;
    private float turretAngle = 0.0f;
    private float cannonAngle = 0.0f;
    
    private float fireCoolDown = 0.0f;
    
    private float angle = 0.0f;
    private float tAngle = 0.0f;
    private float cAngle = 0.0f;
   
    private Node tank = null;
    private Node turret = null;
    private Node cannon = null;
    private Node nozzle = null;
    
    @Override
    public float getForwardSpeed() {
        return forwardSpeed;
    }
    @Override
    public void setForwardSpeed(float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }

    @Override
    public float getBackwardSpeed() {
        return backwardSpeed;
    }
    @Override
    public void setBackwardSpeed(float backwardSpeed) {
        this.backwardSpeed = backwardSpeed;
    }

    @Override
    public float getSteeringSpeed() {
        return steeringSpeed;
    }
    @Override
    public void setSteeringSpeed(float steeringSpeed) {
        this.steeringSpeed = steeringSpeed;
    }

    @Override
    public float getTurretSpeed() {
        return turretSpeed;
    }
    @Override
    public void setTurretSpeed(float turretSpeed) {
        this.turretSpeed = turretSpeed;
    }

    @Override
    public float getCannonSpeed() {
        return cannonSpeed;
    }
    @Override
    public void setCannonSpeed(float cannonSpeed) {
        this.cannonSpeed = cannonSpeed;
    }

    @Override
    public float getReloadTime() {
        return reloadTime;
    }
    @Override
    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    @Override
    public float getThrottle() {
        return throttle;
    }
    @Override
    public void setThrottle(float throttle) {
        this.throttle = Utils.clamp(throttle, -1, 1);
    }

    @Override
    public float getSteering() {
        return steering;
    }
    @Override
    public void setSteering(float steering) {
        this.steering = Utils.clamp(steering, -1, 1);
    }

    @Override
    public void rotateTurret(float turretDeltaAngle) {
        turretAngle = Utils.clamp(turretDeltaAngle, -turretSpeed, turretSpeed);
    }

    @Override
    public void rotateCannon(float cannonDeltaElevation) {
        cannonAngle = Utils.clamp(cannonDeltaElevation, -cannonSpeed, cannonSpeed);
    }

    @Override
    public void fire() {
        if(fireCoolDown <= 0.0f){
            System.out.println("Bang!");
            SpawnControl sc = nozzle.getControl(SpawnControl.class);
            sc.spawn("Bullet");
            fireCoolDown = reloadTime;
        }else
            System.out.println("Cannon is reloading!");
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        tank = spatial instanceof Node ? (Node) spatial : null;
        if(tank != null){
            turret = Utils.getChild(tank, "turret", Node.class);
            cannon = Utils.getChild(turret, "cannon", Node.class);
            nozzle = Utils.getChild(cannon, "nozzle", Node.class);
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f position = tank.getLocalTranslation();
        if(throttle > 0)
            position.addLocal(tank.getLocalRotation().getRotationColumn(2).mult(throttle*forwardSpeed*tpf));
        if(throttle < 0)
            position.addLocal(tank.getLocalRotation().getRotationColumn(2).mult(throttle*backwardSpeed*tpf));
        tank.setLocalTranslation(position);
        
        if(steering != 0)
            tank.setLocalRotation(new Quaternion(new float[]{0.0f, angle += steering*steeringSpeed*tpf, 0.0f}));
        
        if(turretAngle != 0)
            turret.setLocalRotation(new Quaternion(new float[]{0.0f, tAngle += turretAngle*turretSpeed*tpf,0.0f}));
        
        if(cannonAngle != 0){
            
            cannon.setLocalRotation(new Quaternion(new float[]{cAngle += cannonAngle*cannonSpeed*tpf,0.0f,0.0f}));
            
        }
        
        if(fireCoolDown != 0)
            fireCoolDown -= tpf;
            
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
