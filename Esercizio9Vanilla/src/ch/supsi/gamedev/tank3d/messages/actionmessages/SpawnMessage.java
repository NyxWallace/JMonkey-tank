package ch.supsi.gamedev.tank3d.messages.actionmessages;

import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class SpawnMessage extends ActionMessage {

	private static final String ACTION = "Spawn";
	private String name;

	public SpawnMessage() {
		super(ACTION);
	}

	public SpawnMessage(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
