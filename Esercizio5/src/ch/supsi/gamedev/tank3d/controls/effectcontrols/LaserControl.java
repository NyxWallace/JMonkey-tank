package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.controls.sensorcontrols.RangeControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class LaserControl extends AbstractControl implements Cloneable  {
	
	private static SceneGraphVisitor UPDATE_BOUND_VISITOR = new SceneGraphVisitor() {

		@Override
		public void visit(Spatial spatial) {
			spatial.updateModelBound();
		}
	};

	// Defaults
	private static final float DEFAULT_MAX_RANGE = 1000.0f;
	private static final float DEFAULT_WIDTH = 0.3f;
	
	// Globals
	private static Camera globalCamera = null;

	public static Camera getGlobalCamera() {
		return globalCamera;
	}

	public static void setGlobalCamera(Camera globalCamera) {
		LaserControl.globalCamera = globalCamera;
	}

	// Properties
	private float maxRange = DEFAULT_MAX_RANGE;
	private float width = DEFAULT_WIDTH;
	// SceneGraph
	private Geometry laserGeometry = null;
	private RangeControl rangeControl = null;
	private boolean initialized = false;
	
	private void initialize() {
		rangeControl = spatial.getControl(RangeControl.class);
		if (rangeControl == null) {
			return;
		}
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (laserGeometry == null) {
			return;
		}
		if (!initialized) {
			initialize();
			return;
		}
		
		float range = rangeControl.isValid() ? rangeControl.getRange() : maxRange;
		range = FastMath.clamp(range, 0.0f, maxRange);
		laserGeometry.setLocalScale(width, 1.0f, range);
		Utils.traverseUpwards(spatial, UPDATE_BOUND_VISITOR, 3);
		if (globalCamera == null) {
			return;
		}
		
		Vector3f cameraWorldPosition = globalCamera.getLocation();
		Vector3f cameraLocalPosition = new Vector3f();
		spatial.getWorldTransform().transformInverseVector(cameraWorldPosition, cameraLocalPosition);
		
		Vector2f vector = new Vector2f(cameraLocalPosition.getX(), cameraLocalPosition.getY());
		float angle = vector.getAngle() - FastMath.HALF_PI;
		spatial.rotate(new Quaternion(new float[] {0.0f, 0.0f, angle}));
	}
	
	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node laser = spatial instanceof Node ? (Node) spatial : null;
		laserGeometry = Utils.getChild(laser, "laserGeometry", Geometry.class);
	}

	public float getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(float maxRange) {
		this.maxRange = maxRange;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
	
	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		maxRange = capsule.readFloat("maxRange", DEFAULT_MAX_RANGE);
		width = capsule.readFloat("width", DEFAULT_WIDTH);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(maxRange, "maxRange", DEFAULT_MAX_RANGE);
		capsule.write(width, "width", DEFAULT_WIDTH);
	}
}
