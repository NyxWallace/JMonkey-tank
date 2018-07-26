package ch.supsi.gamedev.tank3d;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public abstract class SpatialFactory {
	
	private final AssetManager assetManager;

	protected AssetManager assetManager() {
		return assetManager;
	}
	
	public SpatialFactory(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	public abstract Spatial newSpatial(String name);
}
