package ch.supsi.gamedev.tank3d.messages;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ActionMessage extends ControlMessage {
	
	private String action;

	public ActionMessage() {
		setReliable(true);
	}

	public ActionMessage(String action) {
		this();
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
