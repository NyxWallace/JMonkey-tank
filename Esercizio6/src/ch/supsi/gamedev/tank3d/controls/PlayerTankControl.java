package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class PlayerTankControl extends AbstractControl implements ActionListener, AnalogListener, Cloneable {

	// Defaults
	private static final float DEFAULT_ANALOG_SENSITIVITY = 0.3f;
	// Properties
	private float analogSensitivity = DEFAULT_ANALOG_SENSITIVITY;
	// Transients
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
		
		Node tank = spatial instanceof Node ? (Node) spatial : null;
		Node driverCamera = Utils.getChild(tank, "driverCamera", Node.class);
		driverCameraControl = driverCamera != null ? driverCamera.getControl(CameraControl.class) : null;
		if (driverCameraControl == null) {
			return;
		}
		
		Node turret = Utils.getChild(tank, "turret", Node.class);
		Node gunnerCamera = Utils.getChild(turret, "gunnerCamera", Node.class);
		gunnerCameraControl = gunnerCamera != null ? gunnerCamera.getControl(CameraControl.class) : null;
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
		
		if (driverCamera) {
			driverCameraControl.setEnabled(true);
			driverCamera = false;
		}
		
		if (gunnerCamera) {
			gunnerCameraControl.setEnabled(true);
			gunnerCamera = false;
		}

		Vector2f throttle = new Vector2f();
		throttle.x -= strafeLeft ? 1.0f : 0.0f;
		throttle.x += strafeRight ? 1.0f : 0.0f;
		throttle.y -= backward ? 1.0f : 0.0f;
		throttle.y += forward ? 1.0f : 0.0f;

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
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getAnalogSensitivity() {
		return analogSensitivity;
	}

	public void setAnalogSensitivity(float analogSensitivity) {
		this.analogSensitivity = analogSensitivity;
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case "DriverCamera":
				driverCamera = isPressed;
				break;
			case "GunnerCamera":
				gunnerCamera = isPressed;
				break;
			case "Forward":
				forward = isPressed;
				break;
			case "Backward":
				backward = isPressed;
				break;
			case "StrafeLeft":
				strafeLeft = isPressed;
				break;
			case "StrafeRight":
				strafeRight = isPressed;
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

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		analogSensitivity = capsule.readFloat("analogSensitivity", DEFAULT_ANALOG_SENSITIVITY);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(analogSensitivity, "analogSensitivity", DEFAULT_ANALOG_SENSITIVITY);
	}
}
