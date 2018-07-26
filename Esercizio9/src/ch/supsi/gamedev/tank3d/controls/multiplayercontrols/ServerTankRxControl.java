package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.ReceiverControl;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.FireMessage;
import com.jme3.math.Vector3f;

public class ServerTankRxControl extends ReceiverControl<FireMessage> {

	private ServerTankControl serverTankControl = null;
	private boolean inizialized = false;
	
	private void initialize() {
		serverTankControl = spatial.getControl(ServerTankControl.class);
		if (serverTankControl == null) {
			return;
		}
		inizialized = true;
	}

	@Override
	protected boolean isReady() {
		return inizialized;
	}

	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		if (!inizialized) {
			initialize();
			return;
		}
	}
	
	@Override
	protected void update(FireMessage fireMessage) {
		String projectileName = fireMessage.getProjectileName();
		Vector3f velocity = fireMessage.getVelocity();
		serverTankControl.setProjectileName(projectileName);
		serverTankControl.setVelocity(velocity);
		serverTankControl.fire();
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return FireMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
