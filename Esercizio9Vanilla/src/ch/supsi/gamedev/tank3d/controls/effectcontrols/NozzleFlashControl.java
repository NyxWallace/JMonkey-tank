package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.appstates.AudioManager;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class NozzleFlashControl extends AbstractControl implements Cloneable {

	//AppStates
	private AudioManager audioManager = null;
	//SceneGraph
	private Node nozzleFlash = null;
	private ParticleEmitter directionalFireEmitter = null;
	private ParticleEmitter radialFireEmitter = null;
	private AudioNode audio = null;
	//Transient
	private boolean firing = false;
	
	private void playAudioInstance() {
		if (audioManager != null) {
			audioManager.addSound(audio);
		} else if (Globals.SINGLETON.hasAudioRenderer()) {
			audio.playInstance();
		}
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (nozzleFlash == null || directionalFireEmitter == null || radialFireEmitter == null) {
			return;
		}
		if (firing) {
			playAudioInstance();
			directionalFireEmitter.emitAllParticles();
			radialFireEmitter.emitAllParticles();
			firing = false;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public void fire() {
		if (directionalFireEmitter == null || radialFireEmitter == null) {
			return;
		}
		firing = true;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		AppStateManager stateManager = Globals.SINGLETON.getStateManager();
		audioManager = stateManager != null ? stateManager.getState(AudioManager.class) : null;
		nozzleFlash = spatial instanceof Node ? (Node) spatial : null;
		directionalFireEmitter = Utils.getChild(nozzleFlash, "directionalFireEmitter", ParticleEmitter.class);
		radialFireEmitter = Utils.getChild(nozzleFlash, "radialFireEmitter", ParticleEmitter.class);
		audio = Utils.getChild(nozzleFlash, "audio", AudioNode.class);
	}
}
