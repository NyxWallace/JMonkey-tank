package ch.supsi.gamedev.tank3d.messages.gamemessages;

import ch.supsi.gamedev.tank3d.messages.GameMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class PlayerReadyMessage extends GameMessage {
	
	private String playerName;

	public PlayerReadyMessage() {
	}
	
	public PlayerReadyMessage(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
