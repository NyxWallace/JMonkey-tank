package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.SpatialMessage;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class SpatialTxControl extends UpdateTxControl<SpatialMessage>{

	@Override
	protected SpatialMessage produce() {
		Vector3f position = spatial.getLocalTranslation();
		Quaternion orientation = spatial.getLocalRotation();
		return new SpatialMessage(position, orientation);
	}
}
