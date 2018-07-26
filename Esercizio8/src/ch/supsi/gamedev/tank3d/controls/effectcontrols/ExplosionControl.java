package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;

public class ExplosionControl extends AbstractControl implements Cloneable {
	
	private static enum State {IDLE, FIRING, FIRED};

	//State
	private State state = State.IDLE;
	private float lifeTime = 0.0f;
	private float time = 0.0f;
	//SceneGraph
	private Node explosion = null;
	private ParticleEmitter fireEmitter = null;
	private ParticleEmitter shockwaveEmitter = null;
	private ParticleEmitter sparksEmitter = null;
	private ParticleEmitter smokeEmitter = null;
	private AudioNode audio = null;
	//AppStates
	private AudioManager audioManager = null;
	
	private void playAudioInstance() {
		if (audioManager != null) {
			audioManager.addSound(audio);
		} else {
			audio.playInstance();
		}
	}

	private void updateGravityVector() {
		Vector3f worldUpVector = explosion.getWorldRotation().getRotationColumn(1);
		ParticleEmitter[] particleEmitters = new ParticleEmitter[]{fireEmitter, sparksEmitter, smokeEmitter};
		for (ParticleEmitter particleEmitter : particleEmitters) {
			float length = particleEmitter.getGravity().length();
			particleEmitter.setGravity(worldUpVector.mult(length));
		}
	}

	private float maxHighLife() {
		ParticleEmitter[] particleEmitters = new ParticleEmitter[]{fireEmitter, shockwaveEmitter, sparksEmitter, smokeEmitter};
		float result = 0.0f;
		for (ParticleEmitter particleEmitter : particleEmitters) {
			float highLife = particleEmitter.getHighLife();
			if (highLife > result) {
				result = highLife;
			}
		}
		return result;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (explosion == null || fireEmitter == null || shockwaveEmitter == null || sparksEmitter == null || smokeEmitter == null) {
			return;
		}
		switch (state) {
			case IDLE:
				return;
			case FIRING:
				updateGravityVector();
				fireEmitter.emitAllParticles();
				shockwaveEmitter.emitAllParticles();
				sparksEmitter.emitAllParticles();
				smokeEmitter.emitAllParticles();
				lifeTime = maxHighLife();
				playAudioInstance();
				time = 0.0f;
				state = State.FIRED;
				break;
			case FIRED:
				if (time > lifeTime) {
					explosion.removeFromParent();
				}
				time += tpf;
				break;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public void fire() {
		if (state != State.IDLE || explosion == null || fireEmitter == null || shockwaveEmitter == null || sparksEmitter == null || smokeEmitter == null) {
			return;
		}
		state = State.FIRING;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		audioManager = stateManager != null ? stateManager.getState(AudioManager.class) : null;
		explosion = spatial instanceof Node ? (Node) spatial : null;
		fireEmitter = Utils.getChild(explosion, "fireEmitter", ParticleEmitter.class);
		shockwaveEmitter = Utils.getChild(explosion, "shockwaveEmitter", ParticleEmitter.class);
		sparksEmitter = Utils.getChild(explosion, "sparksEmitter", ParticleEmitter.class);
		smokeEmitter = Utils.getChild(explosion, "smokeEmitter", ParticleEmitter.class);
		audio = Utils.getChild(explosion, "audio", AudioNode.class);
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		super.read(importer);
		InputCapsule capsule = importer.getCapsule(this);
		state = capsule.readEnum("state", State.class, State.IDLE);
		lifeTime = capsule.readFloat("lifeTime", 0.0f);
		time = capsule.readFloat("time", 0.0f);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		super.write(exporter);
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(state, "state", State.IDLE);
		capsule.write(lifeTime, "lifeTime", 0.0f);
		capsule.write(time, "time", 0.0f);
	}
}