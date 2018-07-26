package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.EmitterCircleShape;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class DustControl extends AbstractControl implements Cloneable {
	
	private final EmitterCircleShape emitterCircleShape = new EmitterCircleShape();

	@Override
	protected void controlUpdate(float tpf) {
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node dust = spatial instanceof Node ? (Node) spatial : null;
		ParticleEmitter dustEmitter = Utils.getChild(dust, "dustEmitter", ParticleEmitter.class);
		if (dustEmitter != null) {
			dustEmitter.setShape(emitterCircleShape);
		}
	}
}
