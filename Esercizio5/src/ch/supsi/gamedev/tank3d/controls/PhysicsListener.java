package ch.supsi.gamedev.tank3d.controls;

import com.jme3.scene.control.Control;

public interface PhysicsListener extends Control {
	
	public boolean isEnabled();
	
	public void physicsUpdate(float tpf);
}
