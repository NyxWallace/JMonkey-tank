package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.controls.networkcontrols.ReceiverControl;
import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import ch.supsi.gamedev.tank3d.messages.AppStateMessage;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.KinematicMessage;
import ch.supsi.gamedev.tank3d.messages.RigidBodyMessage;
import ch.supsi.gamedev.tank3d.messages.SpatialMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.SpawnMessage;
import ch.supsi.gamedev.tank3d.messages.TemporalMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DamageMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DieMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.ExplodeMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.FireMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.GameStartMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.PlayerReadyMessage;
import ch.supsi.gamedev.tank3d.messages.gamemessages.WelcomeMessage;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.serializing.Serializer;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class NetworkAppState extends AbstractAppState implements MessageListener {

	private static class LostMessage {

		private final ControlMessage controlMessage;
		private final long timestamp;

		public LostMessage(ControlMessage controlMessage, long timestamp) {
			this.controlMessage = controlMessage;
			this.timestamp = timestamp;
		}

		ControlMessage controlMessage() {
			return controlMessage;
		}

		public long timestamp() {
			return timestamp;
		}
	}
	private static final Class[] MESSAGE_CLASSES = new Class[]{
		WelcomeMessage.class,
		PlayerReadyMessage.class,
		GameStartMessage.class,
		ActionMessage.class,
		FireMessage.class,
		ExplodeMessage.class,
		DamageMessage.class,
		DieMessage.class,
		SpawnMessage.class,
		SpatialMessage.class,
		KinematicMessage.class,
		RigidBodyMessage.class};
	private static final int LOST_MESSAGES_LIFESPAN = 100;
	protected static final String GAME_NAME = "test game";
	protected static final int VERSION = 1;
	protected static final int TCP_PORT = 1024;
	protected static final int UDP_PORT = 2048;
	private final Queue<LostMessage> lostMessages = new ConcurrentLinkedDeque<>();
	private volatile long timestamp = 0;

	private boolean consume(ControlMessage controlMessage) {
		String[] spatialPath = controlMessage.getSpatialPath();
		Node rootNode = Globals.SINGLETON.getRootNode();
		Spatial spatial = Utils.getSpatial(rootNode, spatialPath);
		if (spatial == null) {
			return false;
		}
		boolean result = false;
		int controlsCount = spatial.getNumControls();
		for (int index = 0; index < controlsCount; index++) {
			Control control = spatial.getControl(index);
			if (control instanceof ReceiverControl) {
				ReceiverControl receiverControl = (ReceiverControl) control;
				if (receiverControl.canConsume(controlMessage)) {
					receiverControl.consume(controlMessage);
					result = true;
				}
			}
		}
		return result;
	}

	private boolean consume(AppStateMessage appStateMessage) {
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		ReceiverAppState receiverAppState = stateManager != null ? stateManager.getState(ReceiverAppState.class) : null;
		if (receiverAppState == null || !receiverAppState.canConsume(appStateMessage)) {
			return false;
		}
		receiverAppState.consume(appStateMessage);
		return true;
	}

	protected long getTimestamp() {
		return timestamp;
	}

	protected abstract void send(Message message);

	public NetworkAppState() {
		for (Class messageClass : MESSAGE_CLASSES) {
			Serializer.registerClass(messageClass);
		}
	}

	public abstract boolean isOnline();

	public void sendMessage(Message message) {
		if (!isOnline()) {
			return;
		}
		if (message instanceof TemporalMessage) {
			TemporalMessage temporalMessage = (TemporalMessage) message;
			temporalMessage.setTimestamp(timestamp);
		}
		send(message);
	}

	@Override
	public void messageReceived(Object source, Message message) {
		if (message instanceof ControlMessage) {
			ControlMessage controlMessage = (ControlMessage) message;
			if (!consume(controlMessage)) {
				lostMessages.add(new LostMessage(controlMessage, timestamp));
			}
		} else if (message instanceof AppStateMessage) {
			AppStateMessage appStateMessage = (AppStateMessage) message;
			consume(appStateMessage);
		}
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		timestamp = System.currentTimeMillis();
	}

	@Override
	public void update(float tpf) {
		timestamp = System.currentTimeMillis();
		LostMessage lostMessage = lostMessages.poll();
		if (lostMessage != null) {
			long lostMessageLife = lostMessage.timestamp() - timestamp;
			if (!consume(lostMessage.controlMessage()) && lostMessageLife < LOST_MESSAGES_LIFESPAN) {
				lostMessages.add(lostMessage);
			}
		}
		int lostMessagesCount = lostMessages.size();
		if (lostMessagesCount > 0) {
			System.out.println("lost messages count " + lostMessagesCount);
		}
	}
}
