package ch.supsi.gamedev.tank3d.spatialfactories;

import ch.supsi.gamedev.tank3d.SpatialFactory;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;

public class GridFactory extends SpatialFactory {

	private final Material gridMaterial;

	public GridFactory(AssetManager assetManager) {
		super(assetManager);
		gridMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		gridMaterial.setColor("Color", ColorRGBA.Magenta);
	}
	
	@Override
	public Node newSpatial(String name) {
		Node result = new Node(name);

		Grid grid = new Grid(256, 256, 2.0f);
		
		Geometry geometry = new Geometry("geometry");
		geometry.setMesh(grid);
		geometry.setMaterial(gridMaterial);
		geometry.setLocalTranslation(-128.0f, 0.0f, -128.0f); // offset grid

		result.attachChild(geometry);
		return result;
	}
}
