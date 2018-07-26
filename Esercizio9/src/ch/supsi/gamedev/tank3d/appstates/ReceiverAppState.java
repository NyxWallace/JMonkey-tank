package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.messages.AppStateMessage;
import com.jme3.app.state.AbstractAppState;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class ReceiverAppState<ThisAppStateMessage extends AppStateMessage> extends AbstractAppState {

	private final Queue<ThisAppStateMessage> messages = new ConcurrentLinkedDeque<>();

	protected abstract boolean isReady();

	protected abstract boolean canConsume(AppStateMessage appStateMessage);

	protected abstract void update(ThisAppStateMessage appStateMessage);

	@Override
	public void update(float tpf) {
		ThisAppStateMessage message;
		while (isReady() && (message = messages.poll()) != null) {
			update(message);
		}
	}

	public void consume(AppStateMessage appStateMessage) {
		messages.add((ThisAppStateMessage) appStateMessage);
	}
}
