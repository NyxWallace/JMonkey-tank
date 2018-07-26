package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.RigidBodyMessage;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class RigidBodyRxControl extends ReceiverControl<RigidBodyMessage> {

	@Override
	protected boolean isReady() {
		return true;
	}

	@Override
	protected void update(RigidBodyMessage rigidBodyMessage) {
		RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}
		Vector3f position = rigidBodyMessage.getPosition();
		Quaternion orientation = rigidBodyMessage.getOrientation();
		Vector3f linearVelocity = rigidBodyMessage.getLinearVelocity();
		Vector3f angularVelocity = rigidBodyMessage.getAngularVelocity();
		rigidBodyControl.setPhysicsLocation(position);
		rigidBodyControl.setPhysicsRotation(orientation);
		rigidBodyControl.setLinearVelocity(linearVelocity);
		rigidBodyControl.setAngularVelocity(angularVelocity);
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return RigidBodyMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
