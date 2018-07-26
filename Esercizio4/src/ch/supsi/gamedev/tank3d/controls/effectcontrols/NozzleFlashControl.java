package ch.supsi.gamedev.tank3d.controls.effectcontrols;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class NozzleFlashControl extends AbstractControl implements Cloneable {

	//SceneGraph
	private Node nozzleFlash = null;
	private ParticleEmitter directionalFireEmitter = null;
	private ParticleEmitter radialFireEmitter = null;
	//Transient
	private boolean firing = false;

	@Override
	protected void controlUpdate(float tpf) {
		if (nozzleFlash == null || directionalFireEmitter == null || radialFireEmitter == null) {
			return;
		}
		if (firing) {
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
		nozzleFlash = spatial instanceof Node ? (Node) spatial : null;
		directionalFireEmitter = Utils.getChild(nozzleFlash, "directionalFireEmitter", ParticleEmitter.class);
		radialFireEmitter = Utils.getChild(nozzleFlash, "radialFireEmitter", ParticleEmitter.class);
	}
}
