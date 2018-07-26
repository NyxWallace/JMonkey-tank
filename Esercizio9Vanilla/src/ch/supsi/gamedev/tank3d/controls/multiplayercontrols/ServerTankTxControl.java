package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.networkcontrols.TransmitterControl;
import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DamageMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DieMessage;

public class ServerTankTxControl extends TransmitterControl<ActionMessage> implements TankControl.Listener {

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
	}

	@Override
	public void damaged(float damage) {
		send(new DamageMessage(damage));
	}

	@Override
	public void died() {
		send(new DieMessage());
	}
}
