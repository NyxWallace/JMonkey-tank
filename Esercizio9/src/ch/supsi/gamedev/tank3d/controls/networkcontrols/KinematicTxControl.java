package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.KinematicMessage;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class KinematicTxControl extends UpdateTxControl<KinematicMessage> {

	@Override
	protected KinematicMessage produce() {
		Vector3f position = spatial.getLocalTranslation();
		Quaternion orientation = spatial.getLocalRotation();
		RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);
		Vector3f linearVelocity = new Vector3f();
		Vector3f angularVelocity = new Vector3f();
		if (rigidBodyControl != null) {
			Quaternion worldToLocalRotation = spatial.getWorldRotation().inverse();
			Vector3f worldLinearVelocity = rigidBodyControl.getLinearVelocity();
			Vector3f worldAngularVelocity = rigidBodyControl.getAngularVelocity();
			linearVelocity = worldToLocalRotation.mult(worldLinearVelocity);
			angularVelocity = worldToLocalRotation.mult(worldAngularVelocity);
		}
		return new KinematicMessage(position, orientation, linearVelocity, angularVelocity);
	}
}
