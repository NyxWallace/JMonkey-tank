package ch.supsi.gamedev.tank3d.messages;

import com.jme3.network.serializing.Serializable;

@Serializable
public class GameMessage extends AppStateMessage {

	public GameMessage() {
		setReliable(true);
	}
}
