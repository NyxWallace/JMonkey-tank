package ch.supsi.gamedev.tank3d.controls;

import com.jme3.scene.control.Control;

public interface ProjectileControl extends Control {
	
	public float getStartVelocity();
	
	public void setStartVelocity(float startVelocity);
	
	public void fire();
	
	public void explode();
}
