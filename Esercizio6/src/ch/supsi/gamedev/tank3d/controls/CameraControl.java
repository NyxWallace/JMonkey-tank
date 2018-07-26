package ch.supsi.gamedev.tank3d.controls;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public  class CameraControl extends AbstractControl implements Cloneable {
	
	//Globals
	private static Camera globalCamera = null;
	private static CameraControl globalCameraControl = null;

	public static Camera getGlobalCamera() {
		return globalCamera;
	}

	public static void setGlobalCamera(Camera globalCamera) {
		CameraControl.globalCamera = globalCamera;
	}

	public static CameraControl getGlobalCameraControl() {
		return globalCameraControl;
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		setEnabled(false);
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (globalCamera == null) {
			return;
		}
		Vector3f worldTranslation = spatial.getWorldTranslation();
		Quaternion worldRotation = spatial.getWorldRotation();
		globalCamera.setLocation(worldTranslation);
		globalCamera.setRotation(worldRotation);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled && this != globalCameraControl) {
			if (globalCameraControl != null) {
				globalCameraControl.setEnabled(false);
			}
			globalCameraControl = this;
		}
	}
}
