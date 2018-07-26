package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.ReceiverControl;
import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DamageMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DieMessage;

public class PlayerTankRxControl extends ReceiverControl<ActionMessage> {

	private PlayerTankControl playerTankControl = null;
	private boolean inizialized = false;

	private void initialize() {
		playerTankControl = spatial.getControl(PlayerTankControl.class);
		if (playerTankControl == null) {
			return;
		}
		inizialized = true;
	}

	@Override
	protected boolean isReady() {
		return inizialized;
	}

	@Override
	protected void update(ActionMessage actionMessage) {
		if (actionMessage instanceof DamageMessage) {
			DamageMessage damageMessage = (DamageMessage) actionMessage;
			float damage = damageMessage.getDamage();
			playerTankControl.applyRemoteDamage(damage);
		} else if (actionMessage instanceof DieMessage) {
			DieMessage dieMessage = (DieMessage) actionMessage;
			playerTankControl.die();
		}
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
	public boolean canConsume(ControlMessage controlMessage) {
		return ActionMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
