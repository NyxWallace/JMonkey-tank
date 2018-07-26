package ch.supsi.gamedev.tank3d.messages.actionmessages;

import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class DieMessage extends ActionMessage {
	
	private static final String ACTION = "Die";
	
	public DieMessage() {
		super(ACTION);
	}
}
