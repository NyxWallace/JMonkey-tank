package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.Event;
import com.jme3.scene.control.Control;

public interface EventListener extends Control {
	
	public void event(Event event);
}
