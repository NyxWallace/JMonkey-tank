package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.ClientGameAppState;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.networkcontrols.ReceiverControl;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.SpawnMessage;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public class TankSpawnRxControl extends ReceiverControl<SpawnMessage> {

	private static enum Type {

		PLAYER("Models/Player/TankModel.j3o"), REMOTE("Models/Remote/TankModel.j3o");
		private final String model;

		private Type(String model) {
			this.model = model;
		}

		public String model() {
			return model;
		}
	}

	private ClientGameAppState clientGameAppState = null;
	private SpawnControl spawnControl = null;
	private boolean initialized = false;
	private volatile String name = null;

	private Type getType(String name) {
		String playerName = clientGameAppState.getPlayerName();
		if (name.equals(playerName)) {
			return Type.PLAYER;
		}
		return Type.REMOTE;
	}
	
	private void initialize() {
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		clientGameAppState = stateManager != null ? stateManager.getState(ClientGameAppState.class) : null;
		if (clientGameAppState == null) {
			return;
		}
		spawnControl = spatial.getControl(SpawnControl.class);
		if (spawnControl == null) {
			return;
		}
		initialized = true;
	}

	@Override
	protected boolean isReady() {
		return initialized;
	}

	@Override
	protected void update(SpawnMessage spawnMessage) {
		String name = spawnMessage.getName();
		Type type = getType(name);
		spawnControl.setModel(type.model());
		spawnControl.spawn(name);
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		if (!initialized) {
			initialize();
			return;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return SpawnMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
