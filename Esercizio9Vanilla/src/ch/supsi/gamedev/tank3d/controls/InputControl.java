package ch.supsi.gamedev.tank3d.controls;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public abstract class InputControl extends AbstractControl implements ActionListener, AnalogListener, Cloneable {
	
	public abstract String[] actions();

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
	}

	@Override
	protected void controlUpdate(float tpf) {
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
}
