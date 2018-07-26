package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class ReceiverControl<ThisControlMessage extends ControlMessage> extends AbstractControl implements Cloneable {

	private Queue<ThisControlMessage> messages = null;
	private String[] spatialPath = null;
	private boolean initialized = false;

	private void initialize() {
		spatialPath = Utils.getSpatialPath(spatial);
		initialized = true;
	}
	
	protected abstract boolean isReady();
	
	protected abstract void update(ThisControlMessage controlMessage);
	
	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		ThisControlMessage message;
		while (isReady() && (message = messages.poll()) != null) {
			update(message);
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		messages = new ConcurrentLinkedDeque<>();
	}
	
	public String[] getSpatialPath() {
		return spatialPath;
	}
	
	public abstract boolean canConsume(ControlMessage controlMessage);

	public void consume(ThisControlMessage controlMessage) {
		messages.add(controlMessage);
	}
}

