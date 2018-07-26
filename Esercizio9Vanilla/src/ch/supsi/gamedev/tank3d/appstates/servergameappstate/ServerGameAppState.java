package ch.supsi.gamedev.tank3d.appstates.servergameappstate;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.ReceiverAppState;
import ch.supsi.gamedev.tank3d.appstates.ServerAppState;
import ch.supsi.gamedev.tank3d.controls.multiplayercontrols.TankSpawnTxControl;
import ch.supsi.gamedev.tank3d.messages.AppStateMessage;
import ch.supsi.gamedev.tank3d.messages.GameMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.GameStartMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.PlayerReadyMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.WelcomeMessage;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ServerGameAppState extends ReceiverAppState<GameMessage> implements ConnectionListener {

	private static final int PLAYERS_COUNT = 1;
	private ServerAppState serverAppState = null;
	private final Set<Slot> slots = Collections.synchronizedSet(new HashSet<Slot>());
	private boolean gameStarted = false;
	private boolean initialized = false;

	private Slot getFreeSlot() {
		for (Slot slot : slots) {
			if (slot.isFree()) {
				return slot;
			}
		}
		return null;
	}

	private boolean hasFreeSlot() {
		return getFreeSlot() != null;
	}

	private Player getPlayer(String playerName) {
		for (Slot slot : slots) {
			Player result = slot.getPlayer();
			if (result != null && playerName.equals(result.getName())) {
				return result;
			}
		}
		return null;
	}

	private void initialize() {
		Node rootNode = Globals.SINGLETON.getRootNode();
		Node scene = Utils.getChild(rootNode, "scene", Node.class);
		Spatial tankSpawn1 = Utils.getChild(scene, "tankSpawn1", Spatial.class);
		TankSpawnTxControl tankSpawnTxControl1 = tankSpawn1 != null ? tankSpawn1.getControl(TankSpawnTxControl.class) : null;
		Spatial tankSpawn2 = Utils.getChild(scene, "tankSpawn2", Spatial.class);
		TankSpawnTxControl tankSpawnTxControl2 = tankSpawn2 != null ? tankSpawn2.getControl(TankSpawnTxControl.class) : null;
		if (tankSpawnTxControl1 == null || tankSpawnTxControl2 == null) {
			return;
		}
		if (PLAYERS_COUNT == 1) {
			slots.add(new Slot("player1", tankSpawnTxControl1));
		} else if (PLAYERS_COUNT == 2) {
			slots.add(new Slot("player2", tankSpawnTxControl2));
			slots.add(new Slot("player1", tankSpawnTxControl1));
		} else {
			throw new RuntimeException();
		}
		initialized = true;
	}

	@Override
	protected boolean isReady() {
		return initialized;
	}

	@Override
	protected void update(GameMessage gameMessage) {
		if (gameMessage instanceof PlayerReadyMessage) {
			PlayerReadyMessage playerReadyMessage = (PlayerReadyMessage) gameMessage;
			String playerName = playerReadyMessage.getPlayerName();
			Player player = getPlayer(playerName);
			player.setReady(true);
			System.out.println(player.getName() + "ready");
		}
	}

	@Override
	public boolean canConsume(AppStateMessage appStateMessage) {
		return GameMessage.class.isAssignableFrom(appStateMessage.getClass());
	}

	public boolean isGameReady() {
		for (Slot slot : slots) {
			Player player = slot.getPlayer();
			if (player == null || !player.isReady()) {
				return false;
			}
		}
		return true;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}
	
	public boolean canStartGame() {
		return serverAppState != null && serverAppState.isOnline() && isGameReady();
	}

	public void startGame() {
		serverAppState.sendMessage(new GameStartMessage());
		for (Slot slot : slots) {
			slot.spawn();
		}
		gameStarted = true;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		serverAppState = stateManager.getState(ServerAppState.class);
		if (serverAppState != null && serverAppState.getServer() != null) {
			serverAppState.getServer().addConnectionListener(this);
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (!initialized) {
			initialize();
			return;
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		if (serverAppState != null && serverAppState.getServer() != null) {
			serverAppState.getServer().removeConnectionListener(this);
		}
	}

	@Override
	public void connectionAdded(Server server, HostedConnection hostedConnection) {
		Player player = new Player(hostedConnection);
		Slot slot = getFreeSlot();
		if (slot == null) {
			hostedConnection.close("Server full");
			return;
		}
		slot.setPlayer(player);
		serverAppState.sendMessageTo(new WelcomeMessage(player.getName()), hostedConnection);
	}

	@Override
	public void connectionRemoved(Server server, HostedConnection connection) {
	}
}
