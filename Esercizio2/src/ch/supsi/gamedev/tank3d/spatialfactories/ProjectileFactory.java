package ch.supsi.gamedev.tank3d.spatialfactories;

import ch.supsi.gamedev.tank3d.SpatialFactory;
import ch.supsi.gamedev.tank3d.controls.AdvancedProjectileControl;
import ch.supsi.gamedev.tank3d.controls.KinematicProjectileControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;

public class ProjectileFactory extends SpatialFactory {
	
	private final Cylinder bodyCylinder = new Cylinder(16, 16, 0.3f, 0.7f, true);
	private final Dome tipDome = new Dome(Vector3f.ZERO, 2, 16, 0.3f, false);
	private final Material material;
	
	public ProjectileFactory(AssetManager assetManager) {
		super(assetManager);
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", new ColorRGBA(0.7f, 0.5f, 0.0f, 1.0f));
	}

	@Override
	public Node newSpatial(String name) {
		Geometry bodyGeometry = new Geometry("body", bodyCylinder);
		Geometry tipGeometry = new Geometry("tip", tipDome);
		bodyGeometry.setMaterial(material);
		tipGeometry.setMaterial(material);
		tipGeometry.setLocalTranslation(0.0f, 0.0f, 0.35f);
		tipGeometry.setLocalRotation(new Quaternion(new float[] {FastMath.HALF_PI, 0.0f, 0.0f}));
		
		Node result = new Node(name);
		result.attachChild(bodyGeometry);
		result.attachChild(tipGeometry);
		
		// Add controls
		result.addControl(new KinematicProjectileControl());
		//result.addControl(new AdvancedProjectileControl());
                return result;
	}
}
