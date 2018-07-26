package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.KinematicMessage;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class KinematicRxControl extends ReceiverControl<KinematicMessage> {

	private Vector3f position = null;
	private Quaternion orientation = null;
	
	@Override
	protected boolean isReady() {
		return true;
	}

	@Override
	protected void update(KinematicMessage kinematicMessage) {
		position = kinematicMessage.getPosition();
		orientation = kinematicMessage.getOrientation();
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return KinematicMessage.class.isAssignableFrom(controlMessage.getClass());
	}

	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		if (position != null) {
			spatial.setLocalTranslation(position);
		}
		if (orientation != null) {
			spatial.setLocalRotation(orientation);
		}
	}
}