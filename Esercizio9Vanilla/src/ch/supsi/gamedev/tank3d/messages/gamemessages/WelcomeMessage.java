package ch.supsi.gamedev.tank3d.messages.gamemessages;

import ch.supsi.gamedev.tank3d.messages.GameMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class WelcomeMessage extends GameMessage {
	
	private String playerName;

	public WelcomeMessage() {
	}
	
	public WelcomeMessage(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
