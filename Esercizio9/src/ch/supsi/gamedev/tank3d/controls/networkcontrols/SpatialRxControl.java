package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import ch.supsi.gamedev.tank3d.messages.SpatialMessage;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class SpatialRxControl extends ReceiverControl<SpatialMessage> {

	@Override
	protected boolean isReady() {
		return true;
	}
	
	@Override
	protected void update(SpatialMessage spatialMessage) {
		Vector3f position = spatialMessage.getPosition();
		Quaternion orientation = spatialMessage.getOrientation();
		spatial.setLocalTranslation(position);
		spatial.setLocalRotation(orientation);
	}

	@Override
	public boolean canConsume(ControlMessage controlMessage) {
		return SpatialMessage.class.isAssignableFrom(controlMessage.getClass());
	}
}
