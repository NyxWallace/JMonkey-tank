package ch.supsi.gamedev.tank3d.spatialfactories;

import ch.supsi.gamedev.tank3d.SpatialFactory;
import ch.supsi.gamedev.tank3d.controls.KinematicTankControl;
import ch.supsi.gamedev.tank3d.controls.PlayerTankControl;
import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class TankFactory extends SpatialFactory {

	private final Box hullBox = new Box(3.0f, 1.0f, 4.0f);
	private final Box turretBox = new Box(2.0f, 0.5f, 3.0f);
	private final Cylinder cannonCylinder = new Cylinder(16, 16, 0.3f, 6.0f, true);
	private final ProjectileFactory projectileFactory;
	private final Material hullMaterial;
	private final Material turretMaterial;
	private final Material cannonMaterial;
	
	public TankFactory(AssetManager assetManager) {
		super(assetManager);
		projectileFactory = new ProjectileFactory(assetManager);
		hullMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		turretMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		cannonMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		hullMaterial.setColor("Color", new ColorRGBA(0.0f, 0.5f, 0.2f, 1.0f));
		turretMaterial.setColor("Color", new ColorRGBA(0.0f, 0.6f, 0.1f, 1.0f));
		cannonMaterial.setColor("Color", new ColorRGBA(0.0f, 0.4f, 0.3f, 1.0f));
	}

	@Override
	public Node newSpatial(String name) {
		Geometry hullGeometry = new Geometry("hullGeometry", hullBox);
		Geometry turretGeometry = new Geometry("turretGeometry", turretBox);
		Geometry cannonGeometry = new Geometry("cannonGeometry", cannonCylinder);
		
		hullGeometry.setMaterial(hullMaterial);
		turretGeometry.setMaterial(turretMaterial);
		cannonGeometry.setMaterial(cannonMaterial);

		hullGeometry.setLocalTranslation(0.0f, hullBox.getYExtent(), 0.0f);
		turretGeometry.setLocalTranslation(0.0f, turretBox.getYExtent(), 0.0f);
		cannonGeometry.setLocalTranslation(0.0f, 0.0f, cannonCylinder.getHeight() / 2.0f);

		Node result = new Node(name);
		Node turretNode = new Node("turret");
		Node cannonNode = new Node("cannon");
		Node nozzleNode = new Node("nozzle");

		result.attachChild(turretNode);
		result.attachChild(hullGeometry);
		turretNode.attachChild(cannonNode);
		turretNode.attachChild(turretGeometry);
		cannonNode.attachChild(cannonGeometry);
		cannonNode.attachChild(nozzleNode);
		
		turretNode.setLocalTranslation(0.0f, hullBox.getYExtent() * 2.0f, 0.0f);
		cannonNode.setLocalTranslation(0.0f, turretBox.getYExtent(), turretBox.getZExtent());
		nozzleNode.setLocalTranslation(0.0f, 0.0f, cannonCylinder.getHeight());

		// Controls
		result.addControl(new KinematicTankControl());
		result.addControl(new PlayerTankControl());
		
		SpawnControl spawnControl = new SpawnControl();
		spawnControl.setSpatialFactory(projectileFactory);
		nozzleNode.addControl(spawnControl);
		
		return result;
	}
}
