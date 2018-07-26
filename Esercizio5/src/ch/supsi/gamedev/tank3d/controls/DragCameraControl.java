package ch.supsi.gamedev.tank3d.controls;

import static ch.supsi.gamedev.tank3d.controls.CameraControl.getGlobalCamera;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.IOException;

public class DragCameraControl extends CameraControl {

	//Defaults
	private static final float DEFAULT_DRIFT = 10.0f;
	// Properties
	private float drift = DEFAULT_DRIFT;

	@Override
	protected void controlUpdate(float tpf) {
		if (getGlobalCamera() == null) {
			return;
		}
		Vector3f spatialPosition = spatial.getWorldTranslation();
		Quaternion spatialRotation = spatial.getWorldRotation();
		Vector3f cameraPosition = getGlobalCamera().getLocation();
		Quaternion cameraRotation = getGlobalCamera().getRotation();

		float factor = FastMath.clamp(drift * tpf, 0.0f, 1.0f);
		cameraPosition.addLocal(spatialPosition.subtract(cameraPosition).mult(factor));
		cameraRotation.slerp(spatialRotation, factor);
		getGlobalCamera().setLocation(cameraPosition);
		getGlobalCamera().setRotation(cameraRotation);
	}

	public float getDrift() {
		return drift;
	}

	public void setDrift(float drift) {
		this.drift = drift;
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		drift = capsule.readFloat("drift", DEFAULT_DRIFT);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(drift, "drift", DEFAULT_DRIFT);
	}
}
