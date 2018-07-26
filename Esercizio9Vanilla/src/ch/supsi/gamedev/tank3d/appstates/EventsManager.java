package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.listeners.EventListener;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class EventsManager extends AbstractAppState {

	private static class ThisSceneGraphVisitor implements SceneGraphVisitor {

		private Event event = null;

		public Event getEvent() {
			return event;
		}

		public void setEvent(Event event) {
			this.event = event;
		}

		@Override
		public void visit(Spatial spatial) {
			int controlsCount = spatial.getNumControls();
			for (int index = 0; index < controlsCount; index++) {
			}

			EventListener eventListener = spatial.getControl(EventListener.class);
			if (eventListener != null) {
				eventListener.event(event);
			}
		}
	}
	private final ThisSceneGraphVisitor sceneGraphVisitor = new ThisSceneGraphVisitor();
	private final Queue<Event> events = new ArrayDeque<>();
	private Node rootNode = null;

	public void addEvent(Event event) {
		events.add(event);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		if (application instanceof SimpleApplication) {
			SimpleApplication simpleApplication = (SimpleApplication) application;
			rootNode = simpleApplication.getRootNode();
		}
	}

	@Override
	public void update(float tpf) {
		if (rootNode == null) {
			return;
		}
		Event event = events.poll();
		if (event == null) {
			return;
		}
		Set<EventListener> eventListeners = Utils.descendantsControls(rootNode, EventListener.class);
		for (EventListener eventListener : eventListeners) {
			eventListener.event(event);
		}
	}
}
