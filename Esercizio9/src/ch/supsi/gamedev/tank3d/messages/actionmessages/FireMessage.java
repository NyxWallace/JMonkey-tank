package ch.supsi.gamedev.tank3d.messages.actionmessages;

import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public class FireMessage extends ActionMessage {
	
	private static final String ACTION = "Fire";
	private String projectileName;
	private Vector3f velocity;

	public FireMessage() {
		super(ACTION);
	}

	public FireMessage(String projectileName, Vector3f velocity) {
		this();
		this.projectileName = projectileName;
		this.velocity = velocity;
	}

	public String getProjectileName() {
		return projectileName;
	}

	public void setProjectileName(String projectileName) {
		this.projectileName = projectileName;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
}
