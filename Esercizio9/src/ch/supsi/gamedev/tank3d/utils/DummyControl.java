package ch.supsi.gamedev.tank3d.utils;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class DummyControl extends AbstractControl implements Cloneable{

	@Override
	protected void controlUpdate(float tpf) {
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
}
