package ch.supsi.gamedev.tank3d.controls.listeners;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public interface CollisionListener extends Control {

	public boolean isEnabled();

	public void collision(Spatial spatial, Vector3f point, Vector3f normal);
}
