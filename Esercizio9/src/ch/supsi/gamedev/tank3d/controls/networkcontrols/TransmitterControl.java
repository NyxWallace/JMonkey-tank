package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.NetworkAppState;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class TransmitterControl<ThisControlMessage extends ControlMessage> extends AbstractControl implements Cloneable {

	private String[] spatialPath = null;
	private NetworkAppState networkAppState = null;
	private boolean initialized = false;

	private void initialize() {
		spatialPath = Utils.getSpatialPath(spatial);
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		networkAppState = stateManager != null ?  stateManager.getState(NetworkAppState.class) : null;
		if (networkAppState == null) {
			return;
		}
		initialized = true;
	}
	
	protected void send(ThisControlMessage controlMessage) {
		if (networkAppState == null) {
			return;
		}
		controlMessage.setSpatialPath(spatialPath);
		networkAppState.sendMessage(controlMessage);
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
	}
	
	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
}
