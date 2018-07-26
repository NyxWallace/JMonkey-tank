package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.controls.sensorcontrols.RangeControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class LaserControl extends AbstractControl implements Cloneable  {

	// Globals
	//private static Node globalTerrain = null;
	private static Camera globalCamera = null;
        private static RangeControl rc = null;

	public static Camera getGlobalCamera() {
		return globalCamera;
	}

	public static void setGlobalCamera(Camera globalCamera) {
		LaserControl.globalCamera = globalCamera;
	}
        
        /*public static void setGlobalTerrain(Node globalTerrain) {
            LaserControl.globalTerrain = globalTerrain;
        }

        public static Node getGlobalTerrain() {
            return globalTerrain;
        }*/

	// SceneGraph
	private Geometry laserGeometry = null;
	private boolean initialized = false;
	
	private void initialize() {
		//rc = spatial.getControl(RangeControl.class);
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (laserGeometry == null) {
			return;
		}
                if(rc == null){
                    return;
                }
		if (!initialized) {
			initialize();
			return;
		}
		// Esercizio 3.2 COMPLETARE   
                Vector3f cam = globalCamera.getLocation();
                Vector3f noz = laserGeometry.getWorldTranslation();
                Vector3f diff = noz.subtract(cam);
                
                
               Vector3f xVector = laserGeometry.getWorldRotation().getRotationColumn(0).normalize();
               Vector3f aheadVector = laserGeometry.getWorldRotation().getRotationColumn(2).normalize();
               //Vector3f aheadCamera = globalCamera.getDirection().normalize();
               Vector3f projection = diff.subtract(diff.project(aheadVector)).normalize();
               
               
               float angle = xVector.angleBetween(projection) - FastMath.HALF_PI;
               System.out.println(angle % FastMath.TWO_PI);
               laserGeometry.setLocalRotation(new Quaternion(0.0f,0.0f,-1*angle % FastMath.PI,1.0f));
               
               laserGeometry.setLocalScale(1.0f,1.0f,Float.isInfinite(rc.getRange()) ? 100.0f : rc.getRange());
                
	}
	
	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node laser = spatial instanceof Node ? (Node) spatial : null;
		laserGeometry = Utils.getChild(laser, "laserGeometry", Geometry.class);
                rc = laserGeometry.getControl(RangeControl.class);
                globalCamera = getGlobalCamera();
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
	}
}
