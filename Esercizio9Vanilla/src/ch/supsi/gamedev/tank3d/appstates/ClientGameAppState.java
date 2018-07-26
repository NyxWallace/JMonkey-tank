package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.messages.AppStateMessage;
import ch.supsi.gamedev.tank3d.messages.GameMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.GameStartMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.PlayerReadyMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.WelcomeMessage;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

public class ClientGameAppState extends ReceiverAppState<GameMessage> {
	
	private ClientAppState clientAppState = null;
	private String playerName = null;
	private boolean playerReady = false;
	private boolean gameStarted = false;

	@Override
	protected boolean isReady() {
		return true;
	}

	@Override
	protected void update(GameMessage gameMessage) {
		if (gameMessage instanceof WelcomeMessage) {
			WelcomeMessage welcomeMessage = (WelcomeMessage) gameMessage;
			playerName = welcomeMessage.getPlayerName();
			System.out.println(playerName + " assigned");
		} else if (gameMessage instanceof GameStartMessage) {
			gameStarted = true;
		}
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public boolean isPlayerReady() {
		return playerReady;
	}
	
	public boolean canReadyPlayer() {
		return clientAppState != null && clientAppState.isOnline() && playerName != null;
	}
	
	public void readyPlayer() {
		clientAppState.send(new PlayerReadyMessage(playerName));
		playerReady = true;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		clientAppState = stateManager.getState(ClientAppState.class);
	}
	
	@Override
	public boolean canConsume(AppStateMessage appStateMessage) {
		return GameMessage.class.isAssignableFrom(appStateMessage.getClass());
	}
}
