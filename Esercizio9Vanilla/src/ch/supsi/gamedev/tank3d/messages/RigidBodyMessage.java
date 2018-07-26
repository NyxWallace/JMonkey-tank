package ch.supsi.gamedev.tank3d.messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public class RigidBodyMessage extends KinematicMessage {

	public RigidBodyMessage() {
	}

	public RigidBodyMessage(Vector3f position, Quaternion orientation, Vector3f linearVelocity, Vector3f angularVelocity) {
		super(position, orientation, linearVelocity, angularVelocity);
	}
}
