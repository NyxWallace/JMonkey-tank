package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.ReceiverControl;
import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DamageMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.DieMessage;
import ch.supsi.gamedev.tank3d.messages.actionmessages.FireMessage;
import com.jme3.math.Vector3f;

public class RemoteTankRxControl extends ReceiverControl<ActionMessage> {

	private RemoteTankControl remoteTankControl = null;
	private boolean inizialized = false;

	private void initialize() {
		remoteTankControl = spatial.getControl(RemoteTankControl.class);
		if (remoteTankControl == null) {
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
	protected void update(ActionMessage actionMessage) {
		if (actionMessage instanceof FireMessage) {
			FireMessage fireMessage = (FireMessage) actionMessage;
			String projectileName = fireMessage.getProjectileName();
			Vector3f velocity = fireMessage.getVelocity();
			remoteTankControl.setProjectileName(projectileName);
			remoteTankControl.setVelocity(velocity);
			remoteTankControl.fire();

		} else if (actionMessage instanceof DamageMessage) {
			DamageMessage damageMessage = (DamageMessage) actionMessage;
			float damage = damageMessage.getDamage();
			remoteTankControl.applyRemoteDamage(damage);
		} else if (actionMessage instanceof DieMessage) {
			DieMessage dieMessage = (DieMessage) actionMessage;
			remoteTankControl.die();
		}
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return ActionMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
