package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.Globals;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public  class CameraControl extends AbstractControl implements Cloneable {
	
	private Camera camera = null;
	
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		camera = Globals.SINGLETON.getCamera();
		setEnabled(false);
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (camera == null) {
			return;
		}
		Vector3f worldTranslation = spatial.getWorldTranslation();
		Quaternion worldRotation = spatial.getWorldRotation();
		camera.setLocation(worldTranslation);
		camera.setRotation(worldRotation);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		CameraControl cameraControl = Globals.SINGLETON.getCameraControl();
		if (enabled && this != cameraControl) {
			if (cameraControl != null) {
				cameraControl.setEnabled(false);
			}
			Globals.SINGLETON.setCameraControl(this);
		}
	}
}
