package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.controls.effectcontrols.NozzleFlashControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class PlayerTankControl extends AbstractControl implements ActionListener, AnalogListener, Cloneable {

	// Default
	private static final float DEFAULT_ANALOG_SENSITIVITY = 0.3f;
	// Properties
	private float analogSensitivity = DEFAULT_ANALOG_SENSITIVITY;
	// Transient
	private boolean driverCamera = false;
	private boolean gunnerCamera = false;
	private boolean forward = false;
	private boolean backward = false;
	private boolean strafeLeft = false;
	private boolean strafeRight = false;
	private boolean left = false;
	private boolean right = false;
	private float turretDeltaAngle = 0.0f;
	private float cannonDeltaElevation = 0.0f;
	private boolean firing = false;
	// SceneGraph
	private TankControl tankControl = null;
        private CameraControl driverCameraControl = null;
        private CameraControl gunnerCameraControl = null;
	private boolean initialized = false;

	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
			return;
		}
                
                Node driverCam = Utils.getChild((Node)spatial, "driverCam", Node.class);
                driverCameraControl = driverCam.getControl(CameraControl.class);
		if (driverCameraControl == null) {
			return;
		}
                
                Node gunnerCam = Utils.getChild((Node)spatial, "gunnerCam", Node.class);
                gunnerCameraControl = gunnerCam.getControl(CameraControl.class);
		if (gunnerCameraControl == null) {
			return;
		}
                
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		
		float throttle = 0.0f;
		throttle -= backward ? 1.0f : 0.0f;
		throttle += forward ? 1.0f : 0.0f;
		tankControl.setThrottle(throttle);

		float steering = 0.0f;
		steering -= left ? 1.0f : 0.0f;
		steering += right ? 1.0f : 0.0f;
		tankControl.setSteering(steering);

		tankControl.rotateTurret(turretDeltaAngle);
		tankControl.rotateCannon(cannonDeltaElevation);
		if (firing) {
			tankControl.fire();
		}

		// Reset accumulators
		turretDeltaAngle = 0.0f;
		cannonDeltaElevation = 0.0f;
                
                if(driverCamera)
                    driverCameraControl.setEnabled(true);
                if(gunnerCamera)
                    gunnerCameraControl.setEnabled(true);                  
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case "Forward":
				forward = isPressed;
				break;
			case "Backward":
				backward = isPressed;
				break;
			case "Left":
				left = isPressed;
				break;
			case "Right":
				right = isPressed;
				break;
			case "Fire":
				firing = isPressed;
				break;
                        case "DriverCam":
                            driverCamera = isPressed;
                            break;
                        case "GunnerCam":
                            gunnerCamera = isPressed;
                            break;
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		switch (name) {
			case "TurretLeft":
				turretDeltaAngle += value * analogSensitivity;
				break;
			case "TurretRight":
				turretDeltaAngle -= value * analogSensitivity;
				break;
			case "CannonUp":
				cannonDeltaElevation += value * analogSensitivity;
				break;
			case "CannonDown":
				cannonDeltaElevation -= value * analogSensitivity;
				break;
		}
	}
}
