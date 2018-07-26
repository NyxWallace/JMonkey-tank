package ch.supsi.gamedev.tank3d.messages;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ControlMessage extends TemporalMessage implements Broadcastable {
	
	private String[] spatialPath;

	public String[] getSpatialPath() {
		return spatialPath;
	}

	public void setSpatialPath(String[] spatialPath) {
		this.spatialPath = spatialPath;
	}
}
