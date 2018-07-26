package ch.supsi.gamedev.tank3d.controls.physicscontrols;

import ch.supsi.gamedev.tank3d.controls.PhysicsListener;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.AltimeterControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class AntiGravityControl extends AbstractControl implements PhysicsListener, Cloneable {

	// Defaults
	private static final float DEFAULT_FORCE = 200000.0f; // N
	private static final float DEFAUT_ALTITUDE = 4.0f; // WU
	// Properties
	private float force = DEFAULT_FORCE; // N
	private float altitude = DEFAUT_ALTITUDE; // WU
	// Transient
	private float throttle = 0.0f; // 0..1
	// SceneGraph
	private RigidBodyControl rigidBodyControl = null;
	private AltimeterControl altimeterControl = null;
	private boolean initialized = false;

	private void initialize() {
		rigidBodyControl = Utils.getAncestorControl(spatial, RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}
		altimeterControl = spatial.getControl(AltimeterControl.class);
		if (altimeterControl == null) {
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

		Vector3f position = spatial.getWorldTranslation();
		Vector3f upVector = spatial.getWorldRotation().getRotationColumn(1);

		Vector3f rigidBodyPosition = rigidBodyControl.getPhysicsLocation();
		Vector3f linearVelocity = rigidBodyControl.getLinearVelocity();
		Vector3f angularVelocity = rigidBodyControl.getAngularVelocity();
		Vector3f worldPosition = spatial.getWorldTranslation();

		Vector3f vector = worldPosition.subtract(rigidBodyPosition);
		Vector3f velocity = linearVelocity.add(angularVelocity.cross(vector));
		float upVelocity = velocity.dot(upVector);
		
		float deltaHeight = position.getY() - rigidBodyPosition.getY();
		
		throttle = 0.0f;
		if (altimeterControl.isValid()) {
			float currentAltitude = altimeterControl.getAltitude();
			throttle += (altitude - currentAltitude) / altitude;
		}
		throttle -= upVelocity / 100.0f;
		throttle -= deltaHeight * 5.0f;
		
		throttle = FastMath.clamp(throttle, 0.0f, 1.0f);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getThrottle() {
		return throttle;
	}

	@Override
	public void physicsUpdate(float tpf) {
		float altitudeFactor = 0.0f;
		if (altimeterControl.isValid()) {
			float currentAltitude = altimeterControl.getAltitude();
			altitudeFactor = (altitude - currentAltitude) / altitude;
		}
		
		Vector3f upVector = spatial.getWorldRotation().getRotationColumn(1);
		Vector3f worldPosition = spatial.getWorldTranslation();
		Vector3f physicsLocation = rigidBodyControl.getPhysicsLocation();
		Vector3f appliedForce = upVector.mult(force * FastMath.clamp(throttle, 0.0f, altitudeFactor));
		rigidBodyControl.applyForce(appliedForce, worldPosition.subtract(physicsLocation));
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		force = capsule.readFloat("force", DEFAULT_FORCE);
		altitude = capsule.readFloat("altitude", DEFAUT_ALTITUDE);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(force, "force", DEFAULT_FORCE);
		capsule.write(altitude, "altitude", DEFAUT_ALTITUDE);
	}
}
