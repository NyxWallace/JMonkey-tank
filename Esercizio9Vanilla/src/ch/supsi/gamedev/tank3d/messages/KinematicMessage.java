package ch.supsi.gamedev.tank3d.messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public class KinematicMessage extends SpatialMessage {

	private Vector3f linearVelocity;
	private Vector3f angularVelocity;

	public KinematicMessage() {
	}

	public KinematicMessage(Vector3f position, Quaternion orientation, Vector3f linearVelocity, Vector3f angularVelocity) {
		super(position, orientation);
		this.linearVelocity = linearVelocity;
		this.angularVelocity = angularVelocity;
	}
	
	public Vector3f getLinearVelocity() {
		return linearVelocity;
	}

	public void setLinearVelocity(Vector3f linearVelocity) {
		this.linearVelocity = linearVelocity;
	}

	public Vector3f getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(Vector3f angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
}
