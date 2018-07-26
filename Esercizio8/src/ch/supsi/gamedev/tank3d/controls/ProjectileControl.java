package ch.supsi.gamedev.tank3d.controls;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public interface ProjectileControl extends Control {
	
	public Spatial getOwner();
	
	public void setOwner(Spatial owner);
	
	public float getStartVelocity();
	
	public void setStartVelocity(float startVelocity);
	
	public void fire();
	
	public void explode();
}
