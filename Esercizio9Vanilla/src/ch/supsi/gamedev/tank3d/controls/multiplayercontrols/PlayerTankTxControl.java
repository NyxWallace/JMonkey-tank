package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.networkcontrols.TransmitterControl;
import ch.supsi.gamedev.tank3d.messages.actionmessages.FireMessage;
import com.jme3.math.Vector3f;

public class PlayerTankTxControl extends TransmitterControl<FireMessage> implements TankControl.Listener {

	private TankControl tankControl = null;
	private boolean inizialized = false;
	
	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
			return;
		}
		tankControl.listable().addListener(this);
		inizialized = true;
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
	public void fired(String projectileName) {
		Vector3f velocity = tankControl.getVelocity();
		send(new FireMessage(projectileName, velocity));
	}

	@Override
	public void damaged(float damage) {
	}

	@Override
	public void died() {
	}
}
