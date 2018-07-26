package ch.supsi.gamedev.tank3d.messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public class SpatialMessage extends ControlMessage {

	private Vector3f position;
	private Quaternion orientation;

	public SpatialMessage() {
		setReliable(false);
	}
	
	public SpatialMessage(Vector3f position, Quaternion orientation) {
		this();
		this.position = position;
		this.orientation = orientation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Quaternion getOrientation() {
		return orientation;
	}

	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
	}
}
