/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nuno
 */
public class AdvancedProjectileControl extends AbstractControl implements Cloneable{
	// Default
	private static final float DEFAULT_SPEED = 128.0f;
	private static final float DEFAULT_RANGE = 256.0f;
        private static final float DEFAULT_GRAVITY = 9.81f;
        private static final float DEFAULT_AIR_RES = 3.0f;
	// Properties
	private float speed = DEFAULT_SPEED;
	private float range = DEFAULT_RANGE;
        private float gravity = DEFAULT_GRAVITY;
        private float airRes = DEFAULT_AIR_RES;
	// State
	private float ranDistance = 0.0f;

	@Override
	protected void controlUpdate(float tpf) {

		// Get state
		Vector3f position = spatial.getLocalTranslation();
		Vector3f aheadVector = spatial.getLocalRotation().getRotationColumn(2);

		// Compute deltas
                speed = (float) Math.sqrt(Math.pow(speed*Math.cos(aheadVector.z),2)+Math.pow(speed*Math.sin(aheadVector.z)-(gravity*aheadVector.x)/(speed*Math.cos(aheadVector.z)),2));
		float deltaPosition1 = speed * tpf;
                Vector3f deltaPosition = aheadVector.mult(deltaPosition1);

		// Update state
		spatial.move(deltaPosition);
		ranDistance += deltaPosition1;

                if(position.y  < 0.0f){
                    spatial.removeFromParent();;
                }
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}
}
