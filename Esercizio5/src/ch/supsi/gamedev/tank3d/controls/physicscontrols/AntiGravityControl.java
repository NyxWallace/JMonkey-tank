package ch.supsi.gamedev.tank3d.controls.physicscontrols;

import ch.supsi.gamedev.tank3d.controls.PhysicsListener;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.AltimeterControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import java.io.IOException;

public class AntiGravityControl extends AbstractControl implements PhysicsListener, Cloneable {

    // Defaults
    private static final float DEFAULT_FORCE = 200000.0f; // N
    private static final float DEFAUT_ALTITUDE = 4.0f; // WU
    // Properties
    private float force = DEFAULT_FORCE; // N
    private float altitude = DEFAUT_ALTITUDE; // WU
    // Transient
    private float throttle = 0.0f; // 0..1
    // SceneGraph
    private RigidBodyControl rigidBodyControl = null;
    private AltimeterControl altimeterControl = null;
    private boolean initialized = false;

    private void initialize() {
        rigidBodyControl = Utils.getAncestorControl(spatial, RigidBodyControl.class);
        if (rigidBodyControl == null) {
            return;
        }
        altimeterControl = spatial.getControl(AltimeterControl.class);
        if (altimeterControl == null) {
            return;
        }
        initialized = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!initialized) {
            initialize();
            return;
        }
        float verticalSpeed = rigidBodyControl.getLinearVelocity().y;
        if(altimeterControl.isValid())
            throttle = (float) computeThrottle(altimeterControl.getAltitude(), verticalSpeed*tpf);
        
        // --- Do stuff ---
    }

    public double computeThrottle(double altitude, double verticalSpeed) {
        if(altitude+verticalSpeed < DEFAUT_ALTITUDE){
            return 1.0 - (altitude+verticalSpeed)/DEFAUT_ALTITUDE;
        }
        return 0.0f;
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }

    public float getThrottle() {
        return throttle;
    }

    @Override
    public void physicsUpdate(float tpf) {
        // --- Do stuff ---
        force = throttle * DEFAULT_FORCE;
        Vector3f enginePos = spatial.getWorldTranslation();
        Vector3f rigidCenter = rigidBodyControl.getPhysicsLocation();
        rigidBodyControl.applyForce(new Vector3f(0.0f, force, 0.0f), enginePos.subtract(rigidCenter));

    }

    @Override
    public void read(JmeImporter importer) throws IOException {
        super.read(importer);
        InputCapsule capsule = importer.getCapsule(this);
        force = capsule.readFloat("force", DEFAULT_FORCE);
        altitude = capsule.readFloat("altitude", DEFAUT_ALTITUDE);
    }

    @Override
    public void write(JmeExporter exporter) throws IOException {
        super.write(exporter);
        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(force, "force", DEFAULT_FORCE);
        capsule.write(altitude, "altitude", DEFAUT_ALTITUDE);
    }
}
