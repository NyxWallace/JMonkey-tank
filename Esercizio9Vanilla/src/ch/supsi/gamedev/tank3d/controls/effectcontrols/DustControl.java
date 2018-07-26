package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.EmitterCircleShape;
import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.controls.sensorcontrols.AltimeterControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class DustControl extends AbstractControl implements Cloneable {

	// Defaults
	private static final float DEFAULT_MAX_PARTICLE_PER_SEC = 2000.0f;
	private static final float DEFAULT_MAX_RADIAL_VELOCITY = 20.0f;
	private static final float DEFAULT_MIN_RADIUS = 3.0f;
	private static final float DEFAULT_MAX_RADIUS = 6.0f;
	private static final float DEFAULT_INTENSITY = 1.0f;
	// Properities
	private float maxParticlePerSec = DEFAULT_MAX_PARTICLE_PER_SEC;
	private float maxRadialVelocity = DEFAULT_MAX_RADIAL_VELOCITY;
	private float minRadius = DEFAULT_MIN_RADIUS;
	private float maxRadius = DEFAULT_MAX_RADIUS;
	private float intensity = DEFAULT_INTENSITY;
	// Transient
	private float pitch = 0.0f;
	private boolean dirty = false;
	private Vector3f lastPosition = null;
	// SceneGraph
	private ParticleEmitter dustEmitter = null;
	private final EmitterCircleShape emitterCircleShape = new EmitterCircleShape();
	private RadialParticleInfluencer radialParticleInfluencer = null;
	private AltimeterControl altimeterControl = null;
	private AudioNode audio = null;
	private boolean initialized = false;
	
	private void initialize() {
		altimeterControl = spatial.getControl(AltimeterControl.class);
		if (Globals.SINGLETON.hasAudioRenderer()) {
			audio.play();
		}
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		if (dustEmitter == null || radialParticleInfluencer == null) {
			return;
		}
		if (dirty) {
			float particlePerSec = maxParticlePerSec * intensity;
			float radius = (maxRadius - minRadius) * (1.0f - intensity) + minRadius;
			float radialVelocity = maxRadialVelocity * intensity;
			dustEmitter.setParticlesPerSec(particlePerSec);
			emitterCircleShape.setRadius(radius);
			radialParticleInfluencer.setRadialVelocity(radialVelocity);
			dirty = false;
		}

		if (altimeterControl != null && altimeterControl.isValid()) {
			Vector3f point = altimeterControl.getPoint();
			Vector3f normal = altimeterControl.getNormal();
			Vector3f localPoint = new Vector3f();
			spatial.getWorldTransform().transformInverseVector(point, localPoint);
			dustEmitter.setLocalTranslation(localPoint);

			Vector3f localNormal = spatial.getWorldRotation().inverse().mult(normal);
			Vector3f upVector = dustEmitter.getLocalRotation().getRotationColumn(1);
			Vector3f axis = upVector.cross(localNormal).normalize();
			float angle = FastMath.acos(localNormal.getY());
			Quaternion rotation = new Quaternion();
			rotation.fromAngleAxis(angle, axis);
			dustEmitter.rotate(rotation);
		}

		float newPitch = intensity * 2.0f + 0.05f;
		pitch = pitch * 0.999f + newPitch * 0.001f;
		pitch = FastMath.clamp(pitch, 0.5f, 2.0f);
		audio.setPitch(pitch);
		Vector3f velocity = Vector3f.ZERO;
		Vector3f position = spatial.getWorldTranslation();
		if (lastPosition != null) {
			velocity = position.subtract(lastPosition).divideLocal(tpf);
		}
		audio.setVelocity(velocity);
		lastPosition = position;
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		intensity = FastMath.clamp(intensity, 0.0f, 1.0f);
		this.intensity = intensity;
		dirty = true;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node dust = spatial instanceof Node ? (Node) spatial : null;
		dustEmitter = Utils.getChild(dust, "dustEmitter", ParticleEmitter.class);
		if (dustEmitter != null) {
			dustEmitter.setShape(emitterCircleShape);
		}
		ParticleInfluencer particleInfluencer = dustEmitter != null ? dustEmitter.getParticleInfluencer() : null;
		radialParticleInfluencer = particleInfluencer instanceof ParticleInfluencer ? (RadialParticleInfluencer) particleInfluencer : null;
		audio = Utils.getChild(dust, "audio", AudioNode.class);
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		maxParticlePerSec = capsule.readFloat("maxParticlePerSec", DEFAULT_MAX_PARTICLE_PER_SEC);
		maxRadialVelocity = capsule.readFloat("maxRadialVelocity", DEFAULT_MAX_RADIAL_VELOCITY);
		minRadius = capsule.readFloat("minRadius", DEFAULT_MIN_RADIUS);
		maxRadius = capsule.readFloat("maxRadius", DEFAULT_MAX_RADIUS);
		intensity = capsule.readFloat("intensity", DEFAULT_INTENSITY);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(maxParticlePerSec, "maxParticlePerSec", DEFAULT_MAX_PARTICLE_PER_SEC);
		capsule.write(maxRadialVelocity, "maxRadialVelocity", DEFAULT_MAX_RADIAL_VELOCITY);
		capsule.write(minRadius, "minRadius", DEFAULT_MIN_RADIUS);
		capsule.write(maxRadius, "maxRadius", DEFAULT_MAX_RADIUS);
		capsule.write(intensity, "intensity", DEFAULT_INTENSITY);
	}
}
